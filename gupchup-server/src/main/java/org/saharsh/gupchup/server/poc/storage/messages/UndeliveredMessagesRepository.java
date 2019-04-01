package org.saharsh.gupchup.server.poc.storage.messages;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.saharsh.gupchup.server.model.Message;

public class UndeliveredMessagesRepository {

	private final Map<String, UndeliveredMessages> undeliveredMessagesForUsers = new HashMap<>();

	public void add(String userId, Message message) {

		UndeliveredMessages undeliveredMessages = undeliveredMessagesForUsers.get(userId);
		if (undeliveredMessages == null) {
			synchronized (userId.intern()) {
				undeliveredMessages = undeliveredMessagesForUsers.get(userId);
				if (undeliveredMessages == null) {
					undeliveredMessages = new UndeliveredMessages();
					undeliveredMessagesForUsers.put(userId, undeliveredMessages);
				}
			}
		}

		undeliveredMessages.addMessage(message);

	}

	public List<Message> get(String userId) {

		final UndeliveredMessages undeliveredMessages = undeliveredMessagesForUsers.get(userId);

		if (undeliveredMessages == null) {
			return Collections.emptyList();
		}

		return undeliveredMessages.getUndeliveredMessages();
	}

	public void confirmDelivery(String userId, Set<String> messageIds) {

		final UndeliveredMessages undeliveredMessages = undeliveredMessagesForUsers.get(userId);

		if (undeliveredMessages != null) {
			undeliveredMessages.confirmDelivery(messageIds);
		}
	}

}
