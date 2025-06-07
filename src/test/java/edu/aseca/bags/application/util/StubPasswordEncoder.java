package edu.aseca.bags.application.util;

import edu.aseca.bags.application.interfaces.PasswordEncoder;

public class StubPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(String rawPassword) {
		return "ENC(" + rawPassword + ")";
	}
}