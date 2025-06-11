package edu.aseca.bags.application.interfaces;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.Wallet;
import java.util.Optional;

public interface WalletRepository {
	void save(Wallet wallet);

	boolean existsByEmail(Email email);

	Optional<Wallet> findByEmail(Email email);
}
