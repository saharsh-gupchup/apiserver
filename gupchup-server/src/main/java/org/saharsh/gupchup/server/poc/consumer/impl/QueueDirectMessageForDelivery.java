package org.saharsh.gupchup.server.poc.consumer.impl;

import java.util.List;

import org.saharsh.gupchup.server.model.DirectMessage;
import org.saharsh.gupchup.server.model.RelationshipStatus;
import org.saharsh.gupchup.server.poc.consumer.ConsumableMessage;
import org.saharsh.gupchup.server.poc.consumer.MessageConsumer;
import org.saharsh.gupchup.server.poc.storage.messages.UndeliveredMessagesRepository;
import org.saharsh.gupchup.server.poc.storage.users.Relationship;
import org.saharsh.gupchup.server.poc.storage.users.RelationshipRepository;

public class QueueDirectMessageForDelivery implements MessageConsumer {

	private final RelationshipRepository relationships;
	private final UndeliveredMessagesRepository undeliveredMessages;

	public QueueDirectMessageForDelivery(RelationshipRepository relationships,
			UndeliveredMessagesRepository undeliveredMessages) {
		this.relationships = relationships;
		this.undeliveredMessages = undeliveredMessages;
	}

	@Override
	public boolean willConsume(ConsumableMessage message) {
		return message.getMessage() instanceof DirectMessage;
	}

	@Override
	public ConsumableMessage consume(final ConsumableMessage consumableMessage) {

		final DirectMessage message = (DirectMessage) consumableMessage.getMessage();

		// verify sender and receiver
		final List<Relationship> userRelations = relationships.getRelationshipsForUser(message.getSenderId(),
				RelationshipStatus.CONNECTED);
		if (userRelations == null) {
			// TODO: do proper error handling
			System.out.println("No relationships for user ID: " + message.getSenderId());
		}

		boolean relationshipFound = false;
		for (final Relationship relationship : userRelations) {
			if (relationship.getUserId().equals(message.getReceiverId())) {
				relationshipFound = true;
				break;
			}
		}

		if (relationshipFound) {
			undeliveredMessages.add(message.getReceiverId(), message);
		} else {
			// TODO: do proper error handling
			System.out.println("Forbidden: users (" + message.getSenderId() + " and " + message.getReceiverId()
					+ ") are not connected");
		}

		return consumableMessage;

	}

}
