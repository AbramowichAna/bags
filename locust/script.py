NUM_USERS = 2000
PASSWORD_HASH = "$2b$12$n2zQeCB45nB9fVOgI0dt.O8e8hOEGFmyDE27ANia1u8DDF7SviQEa"

with open("data.sql", "w") as f:
    for i in range(NUM_USERS):
        user_id = 1000 + i
        email = f"user{user_id:04}@test.com"
        f.write(f"INSERT INTO participant_entity (id, participant_type) VALUES ({user_id}, 'WALLET');\n")
        f.write(f"INSERT INTO wallets (id, balance, email, password) VALUES "
                f"({user_id}, 100.00, '{email}', '{PASSWORD_HASH}');\n")

