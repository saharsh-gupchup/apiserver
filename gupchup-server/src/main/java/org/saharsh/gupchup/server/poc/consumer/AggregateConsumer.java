package org.saharsh.gupchup.server.poc.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

import org.saharsh.gupchup.server.model.Message;
import org.saharsh.gupchup.server.poc.publisher.MessagePublisher;;

public class AggregateConsumer implements MessagePublisher.Consumer {

	private final ExecutorService executor;
	private final List<MessageConsumer> consumerChain = new ArrayList<>();

	public AggregateConsumer(ExecutorService executor) {
		this.executor = executor;
	}

	public void appendToConsumerChain(final MessageConsumer consumer) {
		consumerChain.add(consumer);
	}

	@Override
	public void consume(final Message message) {
		executor.execute(() -> {
			ConsumableMessage consumableMessage = new ConsumableMessage(message);
			for (final MessageConsumer consumer : consumerChain) {
				if (consumer.willConsume(consumableMessage)) {
					consumableMessage = consumer.consume(consumableMessage);
				}
			}
		});
	}

}
