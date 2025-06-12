from locust import HttpUser, task, between
import csv
import random

with open("users.csv") as f:
    reader = csv.DictReader(f)
    registered_users = list(reader)

class WalletUser(HttpUser):
    wait_time = between(1, 3)

    def on_start(self):
        user = random.choice(registered_users)
        self.email = user["email"]
        self.password = user["password"]
        self.login()

    def login(self):
        with self.client.post("/auth/login", json={
            "email": self.email,
            "password": self.password
        }, catch_response=True) as response:
            if response.status_code == 200:
                self.token = response.json().get("token")
                self.headers = {"Authorization": f"Bearer {self.token}"}
            else:
                self.token = None
                self.headers = {}
                if response.status_code >= 500:
                    response.failure(f"Server error during login: {response.status_code}")
                else:
                    response.success()  # No contarlo como failure

    @task(7)
    def get_wallet_info(self):
        if self.token:
            with self.client.get("/wallet", headers=self.headers, catch_response=True) as response:
                if response.status_code >= 500:
                    response.failure(f"Server error in get_wallet_info: {response.status_code}")
                else:
                    response.success()

    @task(7)
    def get_transfer_history(self):
        if self.token:
            with self.client.get("/transfer?page=0&size=10", headers=self.headers, catch_response=True) as response:
                if response.status_code >= 500:
                    response.failure(f"Server error in get_transfer_history: {response.status_code}")
                else:
                    response.success()

    @task(2)
    def transfer_funds(self):
        if self.token:
            receiver = random.choice([u for u in registered_users if u["email"] != self.email])
            amount = random.uniform(1.0, 50.0)
            with self.client.post("/transfer", json={
                "toEmail": receiver["email"],
                "amount": amount
            }, headers=self.headers, catch_response=True) as response:
                if response.status_code >= 500:
                    response.failure(f"Server error in transfer_funds: {response.status_code}")
                else:
                    response.success()

    @task(1)
    def create_debin(self):
        if self.token:
            amount = random.uniform(1.0, 2.0)
            with self.client.post("/debin", json={
                "externalServiceName": "bank",
                "serviceType": "bank",
                "externalEmail": self.email,
                "amount": amount
            }, headers=self.headers, catch_response=True) as response:
                if response.status_code >= 500:
                    response.failure(f"Server error in create_debin: {response.status_code}")
                else:
                    response.success()
