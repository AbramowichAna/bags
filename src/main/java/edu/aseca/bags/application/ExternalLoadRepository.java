package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.exception.WalletNotFoundException;

public interface ExternalLoadRepository {
	void save(ExternalLoad externalLoad) throws WalletNotFoundException;
}
