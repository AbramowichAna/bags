package edu.aseca.bags.application.dto;

import edu.aseca.bags.domain.participant.Participant;

public record ParticipantDto(String serviceName, String serviceType, String email) {

	public static ParticipantDto from(Participant participant) {
		return new ParticipantDto(participant.getServiceName(), participant.getServiceType().name(),
				participant.getEmail().address());
	}
}