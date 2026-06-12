import random
from locust import HttpUser, task, between

class ZooUser(HttpUser):
    # Simulate a delay between 0.5 and 2 seconds between tasks
    wait_time = between(0.5, 2.0)
    
    token = None
    headers = {}

    def on_start(self):
        """Runs once when a virtual user starts to authenticate and prepare data"""
        self.login()
        self.register_test_assets()

    def login(self):
        try:
            # Authenticate as Super Admin
            payload = {
                "username": "Super Admin",
                "password": "admin"
            }
            response = self.client.post(
                "/api/admin/auth/login",
                data=payload,
                headers={"Content-Type": "application/x-www-form-urlencoded"}
            )
            if response.status_code == 200:
                self.token = response.json().get("access_token")
                self.headers = {"Authorization": f"Bearer {self.token}"}
        except Exception as e:
            print(f"Login failed: {e}")

    def register_test_assets(self):
        """Attempts to register a test enclosure and device so telemetry POSTs return 201"""
        if not self.token:
            return
            
        # Register a test enclosure (ignore if fails or already exists)
        enclosure_id = 1
        try:
            enc_res = self.client.post(
                "/api/admin/enclosures/",
                json={"name": "Locust Test Enclosure", "geo_location": "Sector L1"},
                headers=self.headers
            )
            if enc_res.status_code == 200:
                enclosure_id = enc_res.json().get("enclosure_id", 1)
        except Exception:
            pass

        # Register a test IoT device
        try:
            self.client.post(
                "/api/admin/devices/register",
                json={
                    "mac_address": "00:1A:2B:3C:4D:5E",
                    "enclosure_id": enclosure_id,
                    "firmware_version": "1.0.0",
                    "status": "Online"
                },
                headers=self.headers
            )
        except Exception:
            pass

    @task(3)
    def view_alerts(self):
        """Simulate users viewing active alerts"""
        if self.token:
            self.client.get("/api/business/alerts/", headers=self.headers)

    @task(2)
    def view_enclosures(self):
        """Simulate users viewing enclosures"""
        if self.token:
            self.client.get("/api/admin/enclosures/", headers=self.headers)

    @task(5)
    def submit_telemetry(self):
        """Simulate IoT controller sending environmental readings"""
        payload = {
            "mac_address": "00:1A:2B:3C:4D:5E",
            "temperature": round(random.uniform(18.0, 32.0), 2),
            "humidity": round(random.uniform(40.0, 85.0), 2),
            "light": round(random.uniform(100.0, 800.0), 2)
        }
        self.client.post("/api/business/telemetry/", json=payload)
