package edu.aseca.bags.application;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.wallet.Wallet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

class InMemoryWalletRepository implements WalletRepository {
	private final Map<Email, Wallet> data = new HashMap<>();

	@Override
	public void save(Wallet wallet) {
		data.put(wallet.getEmail(), wallet);
	}

	@Override
	public boolean existsByEmail(Email email) {
		return data.containsKey(email);
	}

	public Optional<Wallet> findByEmail(Email email) {
		return Optional.ofNullable(data.get(email));
	}
}