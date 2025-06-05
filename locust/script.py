password = "strongPassword123"

with open("users.csv", "w") as f:
    f.write("email,password\n")
    for i in range(1, 2001):
        email = f"user{i:04d}@test.com"
        f.write(f"{email},{password}\n")
