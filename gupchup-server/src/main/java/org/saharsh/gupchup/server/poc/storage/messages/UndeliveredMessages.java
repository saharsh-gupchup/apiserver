package org.saharsh.gupchup.server.poc.storage.messages;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import org.saharsh.gupchup.server.model.Message;

// intentionally package-private
class UndeliveredMessages {

	private long lastTimeMessageAdded = Long.MIN_VALUE;
	private final ConcurrentLinkedDeque<Message> undeliveredMessages = new ConcurrentLinkedDeque<>();

	void addMessage(Message message) {
		lastTimeMessageAdded = System.currentTimeMillis();
		undeliveredMessages.add(message);
	}

	long getLastTimeMessageAdded() {
		return lastTimeMessageAdded;
	}

	List<Message> getUndeliveredMessages() {
		return Arrays.asList(undeliveredMessages.toArray(new Message[] {}));
	}

	void confirmDelivery(java.util.Set<String> messageIds) {
		final Iterator<Message> all = undeliveredMessages.iterator();
		while (all.hasNext()) {
			final Message message = all.next();
			if (messageIds.contains(message.getId())) {
				all.remove();
			}
		}
	}

}
