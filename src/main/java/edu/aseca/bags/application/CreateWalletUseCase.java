package edu.aseca.bags.application;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.email.RawPassword;
import edu.aseca.bags.domain.wallet.Wallet;
import edu.aseca.bags.exception.AlreadyExistingWallet;

public class CreateWalletUseCase {

	private final WalletRepository walletRepository;
	private final PasswordEncoder passwordEncoder;

	public CreateWalletUseCase(WalletRepository walletRepository, PasswordEncoder passwordEncoder) {
		this.walletRepository = walletRepository;
		this.passwordEncoder = passwordEncoder;
	}

	public void create(String emailStr, String passwordStr) throws AlreadyExistingWallet {
		Email email = new Email(emailStr);

		RawPassword rawPassword = new RawPassword(passwordStr);
		String encryptedPassword = passwordEncoder.encode(rawPassword.value());
		Password password = new Password(encryptedPassword);

		if (walletRepository.existsByEmail(email)) {
			throw new AlreadyExistingWallet();
		}

		Wallet wallet = new Wallet(email, password);
		walletRepository.save(wallet);
	}
}
