package edu.aseca.bags.persistence;

import edu.aseca.bags.application.TransferRepository;
import edu.aseca.bags.domain.transaction.Transfer;
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
	public Transfer findById(String id) {
		return jpaRepository.findById(id).map(TransferMapper::toDomain).orElse(null);
	}

	@Override
	public void delete(Transfer transfer) {
		TransferEntity entity = TransferMapper.toEntity(transfer);
		jpaRepository.delete(entity);
	}

	@Override
	public boolean existsById(String id) {
		return jpaRepository.existsById(id);
	}
}