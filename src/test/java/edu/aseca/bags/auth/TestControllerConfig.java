package edu.aseca.bags.auth;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestControllerConfig {

	@Bean
	public TestController testController() {
		return new TestController();
	}
}
