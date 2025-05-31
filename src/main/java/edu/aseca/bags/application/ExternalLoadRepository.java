package edu.aseca.bags.application;

import edu.aseca.bags.domain.transaction.ExternalLoad;

public interface ExternalLoadRepository {
	void save(ExternalLoad externalLoad);
}
