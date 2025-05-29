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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

		JpaTransferRepository transferRepository = new JpaTransferRepository(this.transferRepository,
				this.walletRepository);
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
		JpaTransferRepository transferRepository = new JpaTransferRepository(this.transferRepository,
				this.walletRepository);
		Optional<Transfer> transfer = transferRepository.findByTransferNumber(TransferNumber.of(UUID.randomUUID()));
		assertFalse(transfer.isPresent(), "Should return empty when transfer does not exist");
	}

	@Test
	void findAllTransfersOfaWalletReturnsCorrectTransfers_003() {
		JpaTransferRepository transferRepository = new JpaTransferRepository(this.transferRepository,
				this.walletRepository);
		JpaWalletRepository walletRepository = new JpaWalletRepository(this.walletRepository);

		Wallet wallet1 = new Wallet(new Email("wallet1@gmail.com"), new Password("wallet123"));
		wallet1.addBalance(new Money(100.0));
		Wallet wallet2 = new Wallet(new Email("wallet2@gmail.com"), new Password("wallet123"));
		wallet2.addBalance(new Money(100.0));
		walletRepository.save(wallet1);
		walletRepository.save(wallet2);

		Transfer transfer1 = new Transfer(wallet1, wallet2, new Money(50.0), Instant.now());
		Transfer transfer2 = new Transfer(wallet2, wallet1, new Money(30.0), Instant.now());
		transferRepository.save(transfer1);
		transferRepository.save(transfer2);

		Pageable pageable = Pageable.unpaged();
		Page<Transfer> transfers = transferRepository.findByFromWalletOrToWallet(wallet1, wallet1, pageable);

		assertEquals(2, transfers.getTotalElements(), "Should retrieve all transfers associated with wallet1");
		assertTrue(transfers.getContent().stream().anyMatch(t -> t.transferNumber().equals(transfer1.transferNumber())),
				"Transfer1 should be included");
		assertTrue(transfers.getContent().stream().anyMatch(t -> t.transferNumber().equals(transfer2.transferNumber())),
				"Transfer2 should be included");
	}

	@Test
	void findTransfersWithPagination_004() {
		JpaTransferRepository transferRepository = new JpaTransferRepository(this.transferRepository,
				this.walletRepository);
		JpaWalletRepository walletRepository = new JpaWalletRepository(this.walletRepository);

		Wallet wallet1 = new Wallet(new Email("wallet1@gmail.com"), new Password("wallet123"));
		wallet1.addBalance(new Money(100.0));
		Wallet wallet2 = new Wallet(new Email("wallet2@gmail.com"), new Password("wallet123"));
		wallet2.addBalance(new Money(100.0));
		walletRepository.save(wallet1);
		walletRepository.save(wallet2);

		Transfer transfer1 = new Transfer(wallet1, wallet2, new Money(50.0), Instant.now());
		Transfer transfer2 = new Transfer(wallet2, wallet1, new Money(30.0), Instant.now());
		Transfer transfer3 = new Transfer(wallet1, wallet2, new Money(20.0), Instant.now());
		transferRepository.save(transfer1);
		transferRepository.save(transfer2);
		transferRepository.save(transfer3);

		Pageable pageable = Pageable.ofSize(2).withPage(0);
		Page<Transfer> transfersPage = transferRepository.findByFromWalletOrToWallet(wallet1, wallet1, pageable);

		assertEquals(2, transfersPage.getContent().size(), "Should return 2 transfers in the first page");
		assertEquals(3, transfersPage.getTotalElements(), "Total elements should be 3");
		assertEquals(2, transfersPage.getTotalPages(), "Total pages should be 2");
		assertTrue(transfersPage.getContent().stream()
				.anyMatch(t -> t.transferNumber().equals(transfer1.transferNumber())), "Transfer1 should be included");
		assertTrue(transfersPage.getContent().stream()
				.anyMatch(t -> t.transferNumber().equals(transfer2.transferNumber())), "Transfer2 should be included");
	}

}