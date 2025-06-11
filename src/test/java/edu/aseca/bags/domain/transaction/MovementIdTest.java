package edu.aseca.bags.domain.transaction;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class MovementIdTest {

	@Test
	void shouldCreateTransferNumberWithValidUuid_001() {
		UUID validUuid = UUID.randomUUID();
		MovementId movementId = MovementId.of(validUuid);
		assertEquals(validUuid, movementId.value());
	}

	@Test
	void shouldNotCreateTransferNumberWithNullUuid_002() {
		assertThrows(NullPointerException.class, () -> MovementId.of(null));
	}

	@Test
	void shouldGenerateRandomTransferNumber() {
		MovementId generated = MovementId.random();
		assertNotNull(generated);
		assertNotNull(generated.value());
	}

	@Test
	void shouldRespectValueEquality() {
		UUID uuid = UUID.randomUUID();
		MovementId one = MovementId.of(uuid);
		MovementId two = MovementId.of(uuid);
		assertEquals(one, two);
		assertEquals(one.hashCode(), two.hashCode());
	}

	@Test
	void shouldBeDifferentWhenUuidsDiffer() {
		MovementId one = MovementId.of(UUID.randomUUID());
		MovementId two = MovementId.of(UUID.randomUUID());
		assertNotEquals(one, two);
	}
}
