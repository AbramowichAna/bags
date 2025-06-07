package edu.aseca.bags.persistence;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.email.Password;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.transaction.TransferNumber;
import edu.aseca.bags.persistence.repository.JpaTransferRepository;
import edu.aseca.bags.persistence.repository.JpaWalletRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class JpaTransferRepositoryTest {

	@Autowired
	private JpaTransferRepository transferRepository;

	@Autowired
	private JpaWalletRepository walletRepository;

	@Test
	void saveTransferPersistsDataCorrectly_001() {

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

		Optional<Transfer> transfer = transferRepository.findByTransferNumber(TransferNumber.of(UUID.randomUUID()));
		assertFalse(transfer.isPresent(), "Should return empty when transfer does not exist");
	}

	@Test
	void findAllTransfersOfaWalletReturnsCorrectTransfers_003() {

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

		int page = 0;
		int size = 10;
		List<Transfer> transfers = transferRepository.findByFromWalletOrToWallet(wallet1, wallet1,
				new Pagination(page, size));

		assertEquals(2, transfers.size(), "Should retrieve all transfers associated with wallet1");
		assertTrue(transfers.stream().anyMatch(t -> t.transferNumber().equals(transfer1.transferNumber())),
				"Transfer1 should be included");
		assertTrue(transfers.stream().anyMatch(t -> t.transferNumber().equals(transfer2.transferNumber())),
				"Transfer2 should be included");
	}

	@Test
	void findTransfersWithPagination_004() {

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

		int page = 0;
		int size = 2;
		List<Transfer> transfersPage = transferRepository.findByFromWalletOrToWallet(wallet1, wallet1,
				new Pagination(page, size));

		assertEquals(2, transfersPage.size(), "Should return 2 transfers in the first page");

		List<Transfer> allTransfers = transferRepository.findByFromWalletOrToWallet(wallet1, wallet1,
				new Pagination(0, 10));
		assertEquals(3, allTransfers.size(), "Total elements should be 3");

		int totalPages = (int) Math.ceil((double) allTransfers.size() / size);
		assertEquals(2, totalPages, "Total pages should be 2");

		assertTrue(transfersPage.stream().anyMatch(t -> t.transferNumber().equals(transfer1.transferNumber())),
				"Transfer1 should be included");
		assertTrue(transfersPage.stream().anyMatch(t -> t.transferNumber().equals(transfer2.transferNumber())),
				"Transfer2 should be included");
	}

}