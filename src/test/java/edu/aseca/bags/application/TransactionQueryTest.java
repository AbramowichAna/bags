package edu.aseca.bags.application;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.dto.TransactionView;
import edu.aseca.bags.application.util.InMemoryExternalLoadRepository;
import edu.aseca.bags.application.util.InMemoryTransferRepository;
import edu.aseca.bags.application.util.InMemoryWalletRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.participant.ExternalAccount;
import edu.aseca.bags.domain.participant.ServiceType;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.testutil.TestWalletFactory;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TransactionQueryTest {

	private InMemoryWalletRepository walletRepository;
	private InMemoryTransferRepository transferRepository;
	private InMemoryExternalLoadRepository externalLoadRepository;
	private TransferQuery transferQuery;
	private ExternalLoadQuery externalLoadQuery;
	private TransactionQuery transactionQuery;

	private final String walletEmail = "wallet1@gmail.com";

	@BeforeEach
	void setUp() {
		walletRepository = new InMemoryWalletRepository();
		transferRepository = new InMemoryTransferRepository();
		externalLoadRepository = new InMemoryExternalLoadRepository();

		Wallet wallet = TestWalletFactory.createAndSave(walletRepository, walletEmail, "password", 100);

		transferQuery = new TransferQuery(walletRepository, transferRepository);
		externalLoadQuery = new ExternalLoadQuery(externalLoadRepository);
		transactionQuery = new TransactionQuery(transferQuery, externalLoadQuery);
	}

	@Test
	void shouldReturnCombinedAndSortedTransactions_001() throws WalletNotFoundException {
		Wallet wallet = walletRepository.findByEmail(new Email(walletEmail)).orElseThrow();

		Transfer transfer = new Transfer(wallet, wallet, new Money(10), Instant.parse("2024-06-01T10:00:00Z"));
		transferRepository.save(transfer);

		ExternalAccount extAcc = new ExternalAccount("ext", ServiceType.BANK, "ext@bank.com");
		ExternalLoad externalLoad = new ExternalLoad(UUID.randomUUID(), wallet, new Money(20),
				Instant.parse("2024-06-02T10:00:00Z"), extAcc);
		externalLoadRepository.save(externalLoad);

		List<TransactionView> result = transactionQuery.getTransactions(new Email(walletEmail), new Pagination(0, 10));

		assertEquals(2, result.size());
		assertEquals(20, result.get(0).amount());
		assertEquals(10, result.get(1).amount());
	}

	@Test
	void shouldPaginateTransactions_002() throws WalletNotFoundException {
		Wallet wallet = walletRepository.findByEmail(new Email(walletEmail)).orElseThrow();

		Transfer t1 = new Transfer(wallet, wallet, new Money(10), Instant.parse("2024-06-01T10:00:00Z"));
		Transfer t2 = new Transfer(wallet, wallet, new Money(20), Instant.parse("2024-06-03T10:00:00Z"));
		transferRepository.save(t1);
		transferRepository.save(t2);

		ExternalAccount extAcc = new ExternalAccount("ext", ServiceType.BANK, "ext@bank.com");
		ExternalLoad e1 = new ExternalLoad(UUID.randomUUID(), wallet, new Money(30),
				Instant.parse("2024-06-02T10:00:00Z"), extAcc);
		externalLoadRepository.save(e1);

		List<TransactionView> page0 = transactionQuery.getTransactions(new Email(walletEmail), new Pagination(0, 2));
		assertEquals(2, page0.size());
		assertEquals(20, page0.get(0).amount());
		assertEquals(30, page0.get(1).amount());

		List<TransactionView> page1 = transactionQuery.getTransactions(new Email(walletEmail), new Pagination(1, 2));
		assertEquals(1, page1.size());
		assertEquals(10, page1.get(0).amount());
	}

	@Test
	void shouldReturnEmptyListIfNoTransactions_003() throws WalletNotFoundException {
		List<TransactionView> result = transactionQuery.getTransactions(new Email(walletEmail), new Pagination(0, 10));
		assertTrue(result.isEmpty());
	}
}