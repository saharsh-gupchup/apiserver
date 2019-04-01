package org.saharsh.gupchup.server.poc.consumer;

import java.util.HashMap;

import org.saharsh.gupchup.server.model.Message;

public class ConsumableMessage {

	private final Message message;
	private final HashMap<String, String> context = new HashMap<>();

	public ConsumableMessage(Message message) {
		super();
		this.message = message;
	}

	public Message getMessage() {
		return message;
	}

	public HashMap<String, String> getContext() {
		return context;
	}

	@Override
	public String toString() {
		return "ConsumableMessage [message=" + message + ", context=" + context + "]";
	}

}
