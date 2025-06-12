package edu.aseca.bags.service;

import edu.aseca.bags.application.interfaces.MovementIdGenerator;
import edu.aseca.bags.domain.transaction.MovementId;
import org.springframework.stereotype.Service;

@Service
public class RandomMovementIdGenerator implements MovementIdGenerator {

	@Override
	public MovementId generate() {
		return MovementId.random();
	}
}
