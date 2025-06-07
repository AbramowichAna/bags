package edu.aseca.bags.application.interfaces;

import edu.aseca.bags.domain.email.Email;
import edu.aseca.bags.domain.transaction.ExternalLoad;
import edu.aseca.bags.exception.WalletNotFoundException;
import java.util.List;
import java.util.UUID;

public interface ExternalLoadRepository {
	void save(ExternalLoad externalLoad) throws WalletNotFoundException;

}
