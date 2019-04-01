package org.saharsh.gupchup.server.poc.publisher;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.saharsh.gupchup.server.model.Message;

public class MessagePublisher {

	public interface Consumer {
		void consume(Message message);
	}

	private final Queue<Message> queuedMessages = new ConcurrentLinkedQueue<>();
	private final List<Consumer> consumers = new ArrayList<>();

	public MessagePublisher(ScheduledExecutorService executor) {
		MessagePublisher.scheduleSelfSchedulingFlushJob(this, executor, 50L);
	}

	public MessagePublisher(ScheduledExecutorService executor, long flushIntervalMillis) {
		MessagePublisher.scheduleSelfSchedulingFlushJob(this, executor, flushIntervalMillis);
	}

	public void publishMessage(Message message) {
		queuedMessages.add(message);
	}

	public void addConsumer(Consumer consumer) {
		consumers.add(consumer);
	}

	public void flush() {
		while (!queuedMessages.isEmpty()) {
			final Message popped = queuedMessages.remove();
			consumers.forEach(consumer -> consumer.consume(popped));
		}
	}

	private static Runnable flushJob = null;

	private static void scheduleSelfSchedulingFlushJob(MessagePublisher publisher, ScheduledExecutorService executor,
			long flushIntervalMillis) {

		MessagePublisher.flushJob = () -> {
			publisher.flush();
			if (!executor.isShutdown()) {
				executor.schedule(MessagePublisher.flushJob, flushIntervalMillis, TimeUnit.MILLISECONDS);
			}
		};

		executor.schedule(MessagePublisher.flushJob, flushIntervalMillis, TimeUnit.MILLISECONDS);
	}

}
