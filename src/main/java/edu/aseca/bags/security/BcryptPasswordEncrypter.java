package edu.aseca.bags.security;

import edu.aseca.bags.application.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BcryptPasswordEncrypter implements PasswordEncoder {
	private final org.springframework.security.crypto.password.PasswordEncoder encoder;

	public BcryptPasswordEncrypter(org.springframework.security.crypto.password.PasswordEncoder encoder) {
		this.encoder = encoder;
	}

	@Override
	public String encode(String rawPassword) {
		return encoder.encode(rawPassword);
	}
}
