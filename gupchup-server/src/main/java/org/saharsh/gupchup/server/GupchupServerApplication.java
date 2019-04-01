package org.saharsh.gupchup.server;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import javax.annotation.PreDestroy;

import org.saharsh.gupchup.server.poc.consumer.AggregateConsumer;
import org.saharsh.gupchup.server.poc.consumer.impl.ConfirmMessageDelivery;
import org.saharsh.gupchup.server.poc.consumer.impl.QueueDirectMessageForDelivery;
import org.saharsh.gupchup.server.poc.publisher.MessagePublisher;
import org.saharsh.gupchup.server.poc.storage.messages.UndeliveredMessagesRepository;
import org.saharsh.gupchup.server.poc.storage.users.RelationshipRepository;
import org.saharsh.gupchup.server.poc.storage.users.UsersRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class GupchupServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GupchupServerApplication.class, args);
	}

	@PreDestroy
	public void shutdown() {
		executor().shutdown();
	}

	@Bean
	public ScheduledExecutorService executor() {
		return Executors.newSingleThreadScheduledExecutor();
	}

	@Bean
	public UsersRepository usersRepository() {
		return new UsersRepository();
	}

	@Bean
	public RelationshipRepository relationshipRepository() {
		return new RelationshipRepository();
	}

	@Bean
	public UndeliveredMessagesRepository messagesRepository() {
		return new UndeliveredMessagesRepository();
	}

	@Bean
	public MessagePublisher messagePublisher(ScheduledExecutorService executor,
			RelationshipRepository relationsRepository, UndeliveredMessagesRepository messagesRepository) {

		final QueueDirectMessageForDelivery deliveryConsumer = new QueueDirectMessageForDelivery(relationsRepository,
				messagesRepository);
		final ConfirmMessageDelivery confirmationConsumer = new ConfirmMessageDelivery(messagesRepository);

		final AggregateConsumer consumerChain = new AggregateConsumer(executor);
		consumerChain.appendToConsumerChain(deliveryConsumer);
		consumerChain.appendToConsumerChain(confirmationConsumer);

		final MessagePublisher publisher = new MessagePublisher(executor);
		publisher.addConsumer(consumerChain);

		return publisher;
	}

}
