package edu.aseca.bags;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class BagsApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
	void mainTest() {
		System.setProperty("spring.profiles.active", "test");
		BagsApplication.main(new String[]{});
	}
}
