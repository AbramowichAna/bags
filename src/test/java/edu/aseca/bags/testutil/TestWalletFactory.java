package edu.aseca.bags.testutil;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.wallet.Wallet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class TestWalletFactory {

	private static final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

	public static Wallet createWallet(String email, String rawPassword) {
		return new Wallet(new Email(email), new Password(encoder.encode(rawPassword)));
	}
}
