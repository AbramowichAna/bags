package edu.aseca.bags.persistence;

import edu.aseca.bags.application.TransferRepository;
import edu.aseca.bags.domain.transaction.Transfer;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class JpaTransferRepository implements TransferRepository {

	private final SpringTransferJpaRepository jpaRepository;

	public JpaTransferRepository(SpringTransferJpaRepository jpaRepository) {
		this.jpaRepository = jpaRepository;
	}

	@Override
	public void save(Transfer transfer) {
		TransferEntity entity = TransferMapper.toEntity(transfer);
		jpaRepository.save(entity);
	}

	@Override
	public Optional<Transfer> findById(UUID id) {
		return jpaRepository.findByTransferNumber(id).map(TransferMapper::toDomain);
	}

	@Override
	public void delete(Transfer transfer) {
		TransferEntity entity = TransferMapper.toEntity(transfer);
		jpaRepository.delete(entity);
	}

	@Override
	public boolean existsById(UUID id) {
		return jpaRepository.findByTransferNumber(id).isPresent();
	}
}