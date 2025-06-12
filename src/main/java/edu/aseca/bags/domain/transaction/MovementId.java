package edu.aseca.bags.domain.transaction;

import java.util.Objects;
import java.util.UUID;

public final class MovementId {

	private final UUID value;

	private MovementId(UUID value) {
		this.value = Objects.requireNonNull(value, "Transfer number UUID cannot be null");
	}

	public static MovementId random() {
		return new MovementId(UUID.randomUUID());
	}

	public static MovementId of(UUID uuid) {
		return new MovementId(uuid);
	}

	public UUID value() {
		return value;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if (!(o instanceof MovementId that)) {
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
