package edu.aseca.bags.application.queries;

import edu.aseca.bags.application.dto.MovementView;
import edu.aseca.bags.application.dto.Pagination;
import edu.aseca.bags.application.interfaces.WalletRepository;
import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.participant.Wallet;
import edu.aseca.bags.exception.WalletNotFoundException;
import org.springframework.data.domain.Page;

public class WalletMovementsQuery {

	WalletRepository walletRepository;
	MovementQuery movementQuery;

	public WalletMovementsQuery(WalletRepository walletRepository, MovementQuery movementQuery) {
		this.walletRepository = walletRepository;
		this.movementQuery = movementQuery;
	}

	public Page<MovementView> getMovements(Email walletEmail, Pagination page) throws WalletNotFoundException {

		Wallet wallet = walletRepository.findByEmail(walletEmail).orElseThrow(WalletNotFoundException::new);

		return movementQuery.getMovements(wallet, page);
	}

}
