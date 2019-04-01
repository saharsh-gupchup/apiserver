package org.saharsh.gupchup.server.poc;

import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.saharsh.gupchup.server.model.DeliveryConfirmation;
import org.saharsh.gupchup.server.model.DirectMessage;
import org.saharsh.gupchup.server.model.RelationshipStatus;
import org.saharsh.gupchup.server.model.User;
import org.saharsh.gupchup.server.poc.consumer.AggregateConsumer;
import org.saharsh.gupchup.server.poc.consumer.impl.ConfirmMessageDelivery;
import org.saharsh.gupchup.server.poc.consumer.impl.QueueDirectMessageForDelivery;
import org.saharsh.gupchup.server.poc.publisher.MessagePublisher;
import org.saharsh.gupchup.server.poc.storage.messages.UndeliveredMessagesRepository;
import org.saharsh.gupchup.server.poc.storage.users.RelationshipRepository;
import org.saharsh.gupchup.server.poc.storage.users.UsersRepository;

public final class Driver {

	public static void main(String[] args) throws Exception {

		// Create users
		System.out.println("Creating shweta and saharsh...");
		final User saharsh = new User(UUID.randomUUID().toString(), "saharsh", "Saharsh Singh");
		final User shweta = new User(UUID.randomUUID().toString(), "shweta", "Shweta Rao");

		final UsersRepository userRepository = new UsersRepository();
		userRepository.createUser(saharsh);
		userRepository.createUser(shweta);

		System.out.println("Connecting shweta and saharsh...");
		final RelationshipRepository relationsRepository = new RelationshipRepository();
		relationsRepository.setRelationshipForUser(saharsh.getId(), shweta.getId(),
				RelationshipStatus.CONNECTION_REQUEST_SENT);
		relationsRepository.setRelationshipForUser(shweta.getId(), saharsh.getId(), RelationshipStatus.CONNECTED);

		// Create messaging infrastructure
		final UndeliveredMessagesRepository messagesRepository = new UndeliveredMessagesRepository();
		final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

		final AggregateConsumer consumerChain = new AggregateConsumer(executor);
		final MessagePublisher publisher = new MessagePublisher(executor);
		publisher.addConsumer(consumerChain);

		final QueueDirectMessageForDelivery deliveryConsumer = new QueueDirectMessageForDelivery(relationsRepository,
				messagesRepository);
		consumerChain.appendToConsumerChain(deliveryConsumer);
		final ConfirmMessageDelivery confirmationConsumer = new ConfirmMessageDelivery(messagesRepository);
		consumerChain.appendToConsumerChain(confirmationConsumer);

		// Send some messages
		System.out.println("Saharsh sends 'I Love You to shweta'...");
		publisher.publishMessage(Driver.dm(saharsh, "I Love You", shweta));
		System.out.println("Shweta sends 'I Love You Too'...");
		publisher.publishMessage(Driver.dm(shweta, "I Love You Too!", saharsh));
		System.out.println("Saharsh sends 'I Love You More!'...");
		publisher.publishMessage(Driver.dm(saharsh, "I Love You More!", shweta));
		System.out.println("Shweta sends 'I Love You The Most'...");
		publisher.publishMessage(Driver.dm(shweta, "I Love You The Most!", saharsh));

		// Print messages
		Thread.sleep(1000L);
		messagesRepository.get(shweta.getId()).forEach(message -> {
			System.out.println("Checking Shweta's inbox: ...");
			System.out.println(message);
			publisher.publishMessage(Driver.confirm(shweta, message.getId()));
		});
		messagesRepository.get(saharsh.getId()).forEach(message -> {
			System.out.println("Checking Saharsh's inbox: ...");
			System.out.println(message);
			publisher.publishMessage(Driver.confirm(saharsh, message.getId()));
		});

		// Make sure messages are deleted
		Thread.sleep(1000L);
		System.out.println("Remaining messages for Shweta: " + messagesRepository.get(shweta.getId()).size());
		System.out.println("Remaining messages for Saharsh: " + messagesRepository.get(saharsh.getId()).size());

		// shutdown
		System.out.println("shutting down..");
		executor.shutdown();
		executor.awaitTermination(5, TimeUnit.SECONDS);
		System.out.println("Bye!");
	}

	private static DirectMessage dm(User sender, String content, User receiver) {
		return new DirectMessage(Driver.newId(), sender.getId(), System.currentTimeMillis(), receiver.getId(), content);
	}

	private static DeliveryConfirmation confirm(User sender, String messageId) {
		return new DeliveryConfirmation(Driver.newId(), sender.getId(), System.currentTimeMillis(), messageId);
	}

	private static String newId() {
		return UUID.randomUUID().toString();
	}
}
