package edu.aseca.bags.domain.transaction;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;
import org.junit.jupiter.api.Test;

class TransferNumberTest {

	@Test
	void shouldCreateTransferNumberWithValidUuid_001() {
		UUID validUuid = UUID.randomUUID();
		TransferNumber transferNumber = TransferNumber.of(validUuid);
		assertEquals(validUuid, transferNumber.value());
	}

	@Test
	void shouldNotCreateTransferNumberWithNullUuid_002() {
		assertThrows(NullPointerException.class, () -> TransferNumber.of(null));
	}

	@Test
	void shouldGenerateRandomTransferNumber() {
		TransferNumber generated = TransferNumber.random();
		assertNotNull(generated);
		assertNotNull(generated.value());
	}

	@Test
	void shouldRespectValueEquality() {
		UUID uuid = UUID.randomUUID();
		TransferNumber one = TransferNumber.of(uuid);
		TransferNumber two = TransferNumber.of(uuid);
		assertEquals(one, two);
		assertEquals(one.hashCode(), two.hashCode());
	}

	@Test
	void shouldBeDifferentWhenUuidsDiffer() {
		TransferNumber one = TransferNumber.of(UUID.randomUUID());
		TransferNumber two = TransferNumber.of(UUID.randomUUID());
		assertNotEquals(one, two);
	}
}
