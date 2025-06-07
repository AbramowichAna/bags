package edu.aseca.bags.testutil;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;

public class Defaults {

	public static Email getDefaultEmail() {
		return new Email("default@email.com");
	}

	public static Password getDefaultPassword() {
		return new Password("defaultPassword");
	}
}
