package edu.aseca.bags.domain.transaction;

import java.util.Objects;
import java.util.UUID;

public final class TransferNumber {

	private final UUID value;

	private TransferNumber(UUID value) {
		this.value = Objects.requireNonNull(value, "Transfer number UUID cannot be null");
	}

	public static TransferNumber random() {
		return new TransferNumber(UUID.randomUUID());
	}

	public static TransferNumber of(UUID uuid) {
		return new TransferNumber(uuid);
	}

	public UUID value() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof TransferNumber that)) {
			return false;
		}
		return value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return value.hashCode();
	}

	@Override
	public String toString() {
		return value.toString();
	}
}
