package edu.aseca.bags.persistence;

import static org.junit.jupiter.api.Assertions.*;

import edu.aseca.bags.domain.money.Money;
import edu.aseca.bags.domain.transaction.Transfer;
import edu.aseca.bags.domain.wallet.Wallet;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
public class JpaTransferRepositoryTest {

	@Autowired
	private SpringTransferJpaRepository transferRepository;

	@Autowired
	private SpringWalletJpaRepository walletRepository;

	/*
	 * @Test void saveTransferPersistsDataCorrectly() { JpaTransferRepository
	 * repository = new JpaTransferRepository(transferRepository);
	 * 
	 * WalletEntity fromWalletEntity = new WalletEntity("from@example.com",
	 * "frompass", java.math.BigDecimal.valueOf(500.0)); WalletEntity toWalletEntity
	 * = new WalletEntity("to@example.com", "topass",
	 * java.math.BigDecimal.valueOf(200.0));
	 * 
	 * fromWalletEntity = walletRepository.save(fromWalletEntity); // Save
	 * explicitly toWalletEntity = walletRepository.save(toWalletEntity); // Save
	 * explicitly
	 * 
	 * // Create domain objects with the saved wallet entities Wallet fromWallet =
	 * WalletMapper.toDomain(fromWalletEntity); Wallet toWallet =
	 * WalletMapper.toDomain(toWalletEntity);
	 * 
	 * Instant timestamp = Instant.now(); Transfer transfer = new
	 * Transfer(fromWallet, toWallet, new Money(100.0), timestamp);
	 * 
	 * // Save the transfer repository.save(transfer);
	 * 
	 * // Verify persistence assertEquals(1, transferRepository.count());
	 * TransferEntity savedEntity = transferRepository.findAll().get(0);
	 * assertNotNull(savedEntity); assertNotNull(savedEntity.getId());
	 * assertNotNull(savedEntity.getFromWallet());
	 * assertNotNull(savedEntity.getToWallet()); assertEquals("from@example.com",
	 * savedEntity.getFromWallet().getEmail()); assertEquals("to@example.com",
	 * savedEntity.getToWallet().getEmail()); assertEquals(100.0,
	 * savedEntity.getAmount()); assertEquals(timestamp,
	 * savedEntity.getTimestamp()); }
	 */

	@Test
	void findByIdReturnsCorrectTransfer() {
		// Set up repository
		JpaTransferRepository repository = new JpaTransferRepository(transferRepository);

		// Create and save wallet entities
		WalletEntity fromWalletEntity = new WalletEntity("sender@example.com", "senderpass",
				java.math.BigDecimal.valueOf(300.0));
		WalletEntity toWalletEntity = new WalletEntity("receiver@example.com", "receiverpass",
				java.math.BigDecimal.valueOf(100.0));

		fromWalletEntity = walletRepository.save(fromWalletEntity);
		toWalletEntity = walletRepository.save(toWalletEntity);

		// Create and save transfer entity directly
		Instant timestamp = Instant.now();
		TransferEntity transferEntity = new TransferEntity(fromWalletEntity, toWalletEntity, 50.0, timestamp);
		transferEntity = transferRepository.save(transferEntity);

		// Find the transfer
		Transfer foundTransfer = repository.findById(transferEntity.getId());

		// Verify
		assertNotNull(foundTransfer);
		assertEquals("sender@example.com", foundTransfer.fromWallet().getEmail().address());
		assertEquals("receiver@example.com", foundTransfer.toWallet().getEmail().address());
		assertEquals(50.0, foundTransfer.amount().amount());
		assertEquals(timestamp, foundTransfer.timestamp());
	}

	@Test
	void findByIdReturnsNullWhenNotFound() {
		JpaTransferRepository repository = new JpaTransferRepository(transferRepository);
		Transfer result = repository.findById("non-existent-id");
		assertNull(result);
	}

	@Test
	void existsByIdReturnsTrueWhenTransferExists() {
		// Set up repository
		JpaTransferRepository repository = new JpaTransferRepository(transferRepository);

		// Create and save wallet entities
		WalletEntity fromWalletEntity = new WalletEntity("exists@example.com", "existspass",
				java.math.BigDecimal.valueOf(250.0));
		WalletEntity toWalletEntity = new WalletEntity("check@example.com", "checkpass",
				java.math.BigDecimal.valueOf(150.0));

		fromWalletEntity = walletRepository.save(fromWalletEntity);
		toWalletEntity = walletRepository.save(toWalletEntity);

		// Create and save transfer entity
		TransferEntity transferEntity = new TransferEntity(fromWalletEntity, toWalletEntity, 25.0, Instant.now());
		transferEntity = transferRepository.save(transferEntity);

		// Check if exists
		boolean exists = repository.existsById(transferEntity.getId());

		// Verify
		assertTrue(exists);
	}

	@Test
	void existsByIdReturnsFalseWhenTransferDoesNotExist() {
		JpaTransferRepository repository = new JpaTransferRepository(transferRepository);
		boolean exists = repository.existsById("non-existent-id");
		assertFalse(exists);
	}

	/*
	 * @Test void deleteRemovesTransferFromDatabase() { // Set up repository
	 * JpaTransferRepository repository = new
	 * JpaTransferRepository(transferRepository);
	 * 
	 * // Create and save wallet entities WalletEntity fromWalletEntity = new
	 * WalletEntity("delete@example.com", "deletepass",
	 * java.math.BigDecimal.valueOf(400.0)); WalletEntity toWalletEntity = new
	 * WalletEntity("target@example.com", "targetpass",
	 * java.math.BigDecimal.valueOf(100.0));
	 * 
	 * fromWalletEntity = walletRepository.save(fromWalletEntity); toWalletEntity =
	 * walletRepository.save(toWalletEntity);
	 * 
	 * // Create and save transfer entity TransferEntity transferEntity = new
	 * TransferEntity(fromWalletEntity, toWalletEntity, 75.0, Instant.now());
	 * transferEntity = transferRepository.save(transferEntity); String transferId =
	 * transferEntity.getId();
	 * 
	 * // Verify it was saved assertTrue(transferRepository.existsById(transferId));
	 * 
	 * // Find by ID and then delete Transfer transfer =
	 * repository.findById(transferId); repository.delete(transfer);
	 * 
	 * // Verify deletion assertFalse(transferRepository.existsById(transferId)); }
	 */
}