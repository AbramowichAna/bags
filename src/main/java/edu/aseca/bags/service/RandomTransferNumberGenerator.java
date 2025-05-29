package edu.aseca.bags.service;

import edu.aseca.bags.application.TransferNumberGenerator;
import edu.aseca.bags.domain.transaction.TransferNumber;
import org.springframework.stereotype.Service;

@Service
public class RandomTransferNumberGenerator implements TransferNumberGenerator {

	@Override
	public TransferNumber generate() {
		return TransferNumber.random();
	}
}
