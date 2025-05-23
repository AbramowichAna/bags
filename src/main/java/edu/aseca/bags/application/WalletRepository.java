package edu.aseca.bags.application;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.wallet.Wallet;
import java.util.Optional;

public interface WalletRepository {
	void save(Wallet wallet);

	boolean existsByEmail(Email email);

	Optional<Wallet> findByEmail(Email fromEmail);
}
