import machine
import dht
import time
import math

class HardwareManager:
    """Клас для керування фізичними пристроями (HAL)"""
    def __init__(self, config):
        # Ініціалізація сенсора DHT22
        self.sensor = dht.DHT22(machine.Pin(config['dht_pin']))
        
        # Ініціалізація реле (Heat / Fan)
        # Pin.OUT, value=0 (вимкнено за замовчуванням)
        self.heater = machine.Pin(config['relay_heat_pin'], machine.Pin.OUT, value=0)
        self.fan = machine.Pin(config['relay_fan_pin'], machine.Pin.OUT, value=0)
        
        # Ініціалізація Сервоприводу (PWM)
        self.servo = machine.PWM(machine.Pin(config['servo_pin']), freq=50)
        self.servo.duty(0) # Початкове положення

    def read_sensors(self):
        """Зчитує дані з датчика. Повертає (temp, hum, light) або (None, None, None) при помилці"""
        try:
            self.sensor.measure()
            # Симулюємо рівень освітленості (наприклад, від 100 до 800 лк з невеликим шумом)
            import random
            simulated_light = round(random.uniform(200.0, 700.0), 1)
            return self.sensor.temperature(), self.sensor.humidity(), simulated_light
        except OSError as e:
            print("Sensor Error:", e)
            return None, None, None

    def set_heater(self, state):
        self.heater.value(1 if state else 0)

    def set_fan(self, state):
        self.fan.value(1 if state else 0)

    def move_servo(self, angle):
        """
        Математична обробка для сервоприводу
        Конвертація кута (0-180) в Duty Cycle (0-1023)
        """
        # Формула: Duty = ((angle * 2000 / 180) + 500) / 20000 * 1023
        duty = int(((angle * 2000 / 180) + 500) / 20000 * 1023)
        self.servo.duty(duty)

class LogicController:
    """Клас реалізації бізнес-логіки"""
    def __init__(self, config):
        self.cfg = config
        self.temp_history = []  # Буфер для ковзного середнього температури
        self.hum_history = []   # Буфер для вологості
        self.light_history = [] # Буфер для освітленості
        self.FILTER_SIZE = 5    # Розмір вікна вибірки

    def filter_data(self, raw_val, history_list):
        """
        Фільтрація шумів методом ковзного середнього (SMA)
        """
        if raw_val is None:
            return None
            
        history_list.append(raw_val)
        if len(history_list) > self.FILTER_SIZE:
            history_list.pop(0)
            
        # Розрахунок середнього
        avg_val = sum(history_list) / len(history_list)
        return round(avg_val, 2)

    def process_climate(self, current_temp):
        """
        Алгоритм терморегуляції з гістерезисом
        Розділ 1.2 бізнес-логіки
        Повертає статус: 'heating', 'cooling', 'stable'
        """
        if current_temp is None:
            return "error"

        t_min = self.cfg['temp_min']
        t_max = self.cfg['temp_max']
        hyst = self.cfg['hysteresis']
        
        status = "stable"
        heater_state = False
        fan_state = False

        # Логіка обігріву
        if current_temp <= (t_min - hyst):
            heater_state = True
            status = "heating"
        elif current_temp >= t_min:
            heater_state = False # Вимикаємо тільки коли досягли норми (гістерезис)

        # Логіка охолодження (пріоритет над обігрівом у разі конфлікту)
        if current_temp >= (t_max + hyst):
            fan_state = True
            heater_state = False # Безпека: не можна гріти і охолоджувати одночасно
            status = "cooling"
        elif current_temp <= t_max:
            fan_state = False

        return status, heater_state, fan_state

    def check_feeding_schedule(self):
        """Перевірка часу годування"""
        # Отримуємо поточний час з RTC (локальний час)
        now = time.localtime() # (year, month, mday, hour, minute, second, ...)
        current_time_str = "{:02d}:{:02d}".format(now[3], now[4])
        
        # Перевіряємо, чи є цей час у розкладі
        # Додатково можна додати перевірку, чи не годували ми вже в цю хвилину
        if current_time_str in self.cfg['feeding_schedule']:
            # Тут потрібна логіка "debounce", щоб не годувати 60 секунд поспіль
            # Але для простоти повертаємо True, якщо секунди < 5
            if now[5] < 5: 
                return True
        return False