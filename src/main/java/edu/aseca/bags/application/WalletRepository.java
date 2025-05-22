package edu.aseca.bags.application;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.wallet.Wallet;

public interface WalletRepository {
	void save(Wallet wallet);

	boolean existsByEmail(Email email);
}
