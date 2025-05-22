package edu.aseca.bags.application;

class StubPasswordEncoder implements PasswordEncoder {

	@Override
	public String encode(String rawPassword) {
		return "ENC(" + rawPassword + ")";
	}
}