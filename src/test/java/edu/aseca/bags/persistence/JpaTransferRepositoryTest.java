package edu.aseca.bags.persistence;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.domain.wallet.Wallet;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Transactional
public class JpaTransferRepositoryTest {

	@Autowired
	private SpringTransferJpaRepository transferRepository;

	@Autowired
	private SpringWalletJpaRepository walletRepository;

	@Test
	void saveTransferPersistsDataCorrectly_001() {

		JpaTransferRepository transferRepository = new JpaTransferRepository(this.transferRepository);
		JpaWalletRepository walletRepository = new JpaWalletRepository(this.walletRepository);

		Wallet fromWallet = new Wallet(new Email("wallet1@gmail.com"), new Password("wallet123"));
		fromWallet.addBalance(new Money(100.0));
		Wallet toWallet = new Wallet(new Email("wallet2@gmail.com"), new Password("wallet123"));
		toWallet.addBalance(new Money(100.0));

		walletRepository.save(fromWallet);
		walletRepository.save(toWallet);

		Instant timestamp = Instant.now();
		Money transferAmount = new Money(50.0);
		Transfer transfer = new Transfer(fromWallet, toWallet, transferAmount, timestamp);

		transferRepository.save(transfer);

		Optional<Transfer> savedEntityOptional = transferRepository.findByTransferNumber(transfer.transferNumber());

		assertTrue(savedEntityOptional.isPresent(), "Transfer should be saved in the repository");
		Transfer savedTransfer = savedEntityOptional.get();
		assertEquals(transfer.fromWallet().getEmail(), savedTransfer.fromWallet().getEmail(),
				"From wallet should match");
		assertEquals(transfer.toWallet().getEmail(), savedTransfer.toWallet().getEmail(), "To wallet should match");
		assertEquals(transfer.amount().amount(), savedTransfer.amount().amount(), "Transfer amount should match");
		assertEquals(transfer.timestamp(), savedTransfer.timestamp(), "Transfer timestamp should match");
	}

	@Test
	void findByIdReturnsEmptyWhenTransferDoesNotExist_002() {
		JpaTransferRepository transferRepository = new JpaTransferRepository(this.transferRepository);
		Optional<Transfer> transfer = transferRepository.findByTransferNumber(TransferNumber.of(UUID.randomUUID()));
		assertFalse(transfer.isPresent(), "Should return empty when transfer does not exist");
	}
}