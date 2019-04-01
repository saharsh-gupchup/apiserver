package org.saharsh.gupchup.server.services;

import java.util.List;

import org.saharsh.gupchup.server.model.Message;
import org.saharsh.gupchup.server.poc.publisher.MessagePublisher;
import org.saharsh.gupchup.server.poc.storage.messages.UndeliveredMessagesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessagesService {

	private final MessagePublisher publisher;
	private final UndeliveredMessagesRepository undeliverdMessagesRepository;

	@Autowired
	public MessagesService(MessagePublisher publisher, UndeliveredMessagesRepository undeliverdMessagesRepository) {
		this.publisher = publisher;
		this.undeliverdMessagesRepository = undeliverdMessagesRepository;
	}

	public void deliverMessage(Message message) {
		publisher.publishMessage(message);
	}

	public List<Message> getNewMessagesForUser(String userId) {
		return undeliverdMessagesRepository.get(userId);
	}

}
