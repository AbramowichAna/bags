package edu.aseca.bags.domain;

import edu.aseca.bags.domain.transaction.Transaction;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransferRepository extends JpaRepository<Transaction, UUID> {
}
