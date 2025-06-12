
# Locust

Install locust
```bash
pip3 install locust
```
Install uv and then

```bash
uvx locust -V
```
Run tests

```bash
uvx locust -f locustfile.py --host=http://localhost:8080
```

Load testing

```bash
uvx locust -f locustfile.py --host=http://localhost:8080 --users 100 --spawn-rate 10 --run-time 10m
```

Stress testing

```bash
uvx locust -f locustfile.py --host=http://localhost:8080 --users 1000 --spawn-rate 50 --run-time 15m
```