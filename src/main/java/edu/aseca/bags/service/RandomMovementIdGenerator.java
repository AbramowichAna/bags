package edu.aseca.bags.service;

import edu.aseca.bags.application.interfaces.MovementIdGenerator;
import edu.aseca.bags.domain.transaction.TransferNumber;
import org.springframework.stereotype.Service;

@Service
public class RandomMovementIdGenerator implements MovementIdGenerator {

	@Override
	public TransferNumber generate() {
		return TransferNumber.random();
	}
}
