package edu.aseca.bags.persistence.mapper.participantstrategy;

import edu.aseca.bags.domain.participant.Participant;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.exception.WalletNotFoundException;
import edu.aseca.bags.persistence.entity.ParticipantEntity;
import edu.aseca.bags.persistence.entity.WalletEntity;
import edu.aseca.bags.persistence.repository.SpringWalletJpaRepository;
import org.springframework.stereotype.Component;

@Component
public class WalletResolverStrategy implements ParticipantEntityResolverStrategy {

	private final SpringWalletJpaRepository repository;

	public WalletResolverStrategy(SpringWalletJpaRepository repository) {
		this.repository = repository;
	}

	@Override
	public boolean supports(Participant participant) {
		return participant instanceof Wallet;
	}

	@Override
	public ParticipantEntity resolve(Participant participant) {
		Wallet wallet = (Wallet) participant;
		return repository.findByEmail(wallet.getEmail().address()).orElseThrow(IllegalArgumentException::new);
	}
}
