import json
import time
import sys
import os
from datetime import datetime, timedelta, timezone 
import paho.mqtt.client as mqtt
from sqlalchemy import delete

# --- Налаштування шляхів (щоб бачити dependencies.py) ---
current_dir = os.path.dirname(os.path.abspath(__file__))
parent_dir = os.path.dirname(current_dir)
if parent_dir not in sys.path:
    sys.path.insert(0, parent_dir)

from dependencies import SessionLocal
from models import SensorReading, IoTDevice, Animal, ClimateProfile, Alert, Enclosure, Species

# --- КОНФІГУРАЦІЯ ---
MQTT_BROKER = "broker.hivemq.com"
MQTT_PORT = 1883
MQTT_TOPIC = "zoo/telemetry"

# Словник для відстеження часу останнього запису кожного пристрою
# Format: {device_id: last_save_timestamp}
last_save_time = {}

SAVE_INTERVAL_SECONDS = 180 # 3 хвилини
DATA_RETENTION_HOURS = 24   # Зберігати дані за 24 години
ALERT_THRESHOLD = 5.0       # Поріг відхилення для алерту (градуси)

def clean_old_data(db_session):
    """Видаляє записи, старіші за DATA_RETENTION_HOURS."""
    try:
        cutoff_time = datetime.now(timezone.utc) - timedelta(hours=DATA_RETENTION_HOURS)
        # Використовуємо ORM delete
        statement = delete(SensorReading).where(SensorReading.timestamp < cutoff_time)
        result = db_session.execute(statement)
        db_session.commit()
        
        if result.rowcount > 0:
            print(f"🧹 [CLEANUP] Видалено {result.rowcount} старих записів (старші {DATA_RETENTION_HOURS} год).")
    except Exception as e:
        print(f"⚠️ Cleanup Error: {e}")

def check_and_create_alert(db_session, device_id, current_temp):
    """
    Перевіряє, чи виходить температура за межі норми.
    Якщо так - створює запис в таблиці Alert.
    """
    try:
        # 1. Знаходимо пристрій та його вольєр
        device = db_session.query(IoTDevice).filter(IoTDevice.device_id == device_id).first()
        if not device or not device.enclosure_id:
            return

        # 2. Знаходимо тварину у цьому вольєрі -> її вид -> кліматичний профіль
        # (Використовуємо join для ефективності)
        # SELECT min_temp, max_temp FROM ...
        result = db_session.query(ClimateProfile.min_temperature, ClimateProfile.max_temperature)\
            .join(Species, ClimateProfile.species_id == Species.species_id)\
            .join(Animal, Species.species_id == Animal.species_id)\
            .filter(Animal.enclosure_id == device.enclosure_id)\
            .first()

        if not result:
            return # Немає норм - немає алертів

        min_temp, max_temp = result
        
        # 3. Перевірка на відхилення
        alert_msg = None
        alert_type = None
        
        if current_temp < (min_temp - ALERT_THRESHOLD):
            diff = round(min_temp - current_temp, 1)
            alert_type = "Critical Low Temp"
            alert_msg = f"Температура {current_temp}°C нижче норми на {diff}°C (Min: {min_temp}°C)"
            
        elif current_temp > (max_temp + ALERT_THRESHOLD):
            diff = round(current_temp - max_temp, 1)
            alert_type = "Critical High Temp"
            alert_msg = f"Температура {current_temp}°C вище норми на {diff}°C (Max: {max_temp}°C)"

        # 4. Якщо є проблема - записуємо в БД та консоль
        if alert_msg: 
            # Перевіряємо, чи не було такого ж алерту за останні 10 хвилин (щоб не спамити)
            last_alert = db_session.query(Alert)\
                .filter(Alert.enclosure_id == device.enclosure_id)\
                .filter(Alert.status == "New")\
                .order_by(Alert.timestamp.desc())\
                .first()
            
            now_utc = datetime.now(timezone.utc).replace(tzinfo=None) # Приводимо до naive для порівняння з БД
            
            # Якщо останній алерт був недавно і про те саме - пропускаємо
            if last_alert and last_alert.alert_type == alert_type and \
               (now_utc - last_alert.timestamp).total_seconds() < 600:
                return

            print(f"🚨 [ALERT] {alert_msg}")
            
            new_alert = Alert(
                enclosure_id=device.enclosure_id,
                alert_type=alert_type,
                message=alert_msg,
                status="New",
                timestamp=now_utc
            )
            db_session.add(new_alert)
            db_session.commit()

    except Exception as e:
        print(f"⚠️ Alert Check Error: {e}")

def save_to_db(data: dict):
    """
    Зберігає дані, якщо пройшло достатньо часу з останнього запису.
    Також запускає очищення старих даних та перевірку алертів.
    """
    global last_save_time
    
    # 1. Отримуємо ID пристрою з повідомлення
    aviary_str = str(data.get("aviary_id", "1"))
    try:
        import re
        digits = re.findall(r'\d+', aviary_str)
        device_id = int(digits[0]) if digits else 1
    except:
        device_id = 1

    # 2. Отримуємо позначку часу з повідомлення або використовуємо поточний UTC
    payload_ts = data.get("timestamp")
    if payload_ts:
        # payload_ts is a float (UNIX epoch seconds)
        reading_time = datetime.fromtimestamp(payload_ts, tz=timezone.utc).replace(tzinfo=None)
        msg_time_seconds = payload_ts
    else:
        reading_time = datetime.now(timezone.utc).replace(tzinfo=None)
        msg_time_seconds = time.time()

    # 3. Перевірка інтервалу (Throttle)
    last_time = last_save_time.get(device_id, 0)
    if msg_time_seconds - last_time < SAVE_INTERVAL_SECONDS:
        return

    # 4. Збереження в БД
    db = SessionLocal()
    try:
        # Спочатку почистимо старі дані
        clean_old_data(db)

        current_temp = float(data.get("temp"))
        light_value = float(data.get("light", 0.0))

        # Створюємо новий запис
        record = SensorReading(
            device_id=device_id,
            temperature_val=current_temp,
            humidity_val=data.get("hum"),
            light_val=light_value,
            timestamp=reading_time
        )
        
        db.add(record)
        
        # --- ПЕРЕВІРКА НА АЛЕРТИ ---
        check_and_create_alert(db, device_id, current_temp)
        
        db.commit()
        
        # Оновлюємо час останнього запису
        last_save_time[device_id] = msg_time_seconds
        print(f"💾 [DB SAVED] Device {device_id}: T={current_temp}°C, L={light_value} lx (Time: {reading_time})")
        
    except Exception as e:
        print(f"❌ DB Save Error: {e}")
        db.rollback()
    finally:
        db.close()

# --- MQTT CALLBACKS ---

def on_connect(client, userdata, flags, rc, properties=None):
    print(f"✅ Connected to MQTT Broker ({MQTT_BROKER}) with code {rc}")
    client.subscribe(MQTT_TOPIC)
    print(f"👂 Listening on topic: {MQTT_TOPIC}")

def on_message(client, userdata, msg):
    try:
        payload = msg.payload.decode()
        data = json.loads(payload)
        save_to_db(data)
    except Exception as e:
        print(f"⚠️ Message Error: {e}")

# --- ЗАПУСК ---

if __name__ == "__main__":
    print("🚀 Starting MQTT Worker (Logger & Alert System)...")
    print(f"⚙️  Policy: Save every {SAVE_INTERVAL_SECONDS}s, Keep {DATA_RETENTION_HOURS}h, Alert diff: {ALERT_THRESHOLD}°C")
    
    client = mqtt.Client(mqtt.CallbackAPIVersion.VERSION2)
    client.on_connect = on_connect
    client.on_message = on_message
    
    try:
        client.connect(MQTT_BROKER, MQTT_PORT, 60)
        client.loop_forever()
    except KeyboardInterrupt:
        print("\n🛑 Worker stopped.")
    except Exception as e:
        print(f"❌ Critical Error: {e}")