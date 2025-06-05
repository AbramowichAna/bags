import csv
import random
from locust import HttpUser, task, between

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
        response = self.client.post("/auth/login", json={
            "email": self.email,
            "password": self.password
        })
        if response.status_code == 200:
            self.token = response.json().get("token")
            self.headers = {"Authorization": f"Bearer {self.token}"}
        else:
            self.token = None
            self.headers = {}

    @task(5)
    def get_wallet_info(self):
        if self.token:
            self.client.get("/wallet", headers=self.headers)

    @task(5)
    def get_transfer_history(self):
        if self.token:
            self.client.get("/transfer?page=0&size=10", headers=self.headers)

    @task(1)
    def transfer_funds(self):
        if self.token:
            receiver = random.choice([u for u in registered_users if u["email"] != self.email])
            amount = random.uniform(1.0, 50.0)
            self.client.post("/transfer", json={
                "toEmail": receiver["email"],
                "amount": amount
            }, headers=self.headers)
