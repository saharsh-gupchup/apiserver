package org.saharsh.gupchup.server.poc.consumer;

public interface MessageConsumer {

	boolean willConsume(ConsumableMessage message);

	ConsumableMessage consume(ConsumableMessage message);

}
