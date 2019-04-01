package org.saharsh.gupchup.server.poc.consumer.impl;

import org.saharsh.gupchup.server.model.DeliveryConfirmation;
import org.saharsh.gupchup.server.poc.consumer.ConsumableMessage;
import org.saharsh.gupchup.server.poc.consumer.MessageConsumer;
import org.saharsh.gupchup.server.poc.storage.messages.UndeliveredMessagesRepository;

public class ConfirmMessageDelivery implements MessageConsumer {

	private final UndeliveredMessagesRepository undeliveredMessages;

	public ConfirmMessageDelivery(UndeliveredMessagesRepository undeliveredMessages) {
		this.undeliveredMessages = undeliveredMessages;
	}

	@Override
	public boolean willConsume(ConsumableMessage message) {
		return message.getMessage() instanceof DeliveryConfirmation;
	}

	@Override
	public ConsumableMessage consume(final ConsumableMessage consumableMessage) {
		final DeliveryConfirmation confirmation = (DeliveryConfirmation) consumableMessage.getMessage();
		undeliveredMessages.confirmDelivery(confirmation.getSenderId(), confirmation.getMessageIdsDelivered());
		return consumableMessage;
	}

}
