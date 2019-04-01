package org.saharsh.gupchup.server.poc.storage.users;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import org.saharsh.gupchup.server.model.RelationshipStatus;
import org.saharsh.gupchup.server.poc.storage.users.exceptions.InvalidRelationshipStateException;

public class RelationshipRepository {

	private final Map<String, Map<String, Relationship>> userRelationships = new HashMap<>();

	public List<Relationship> getRelationshipsForUser(String userId, RelationshipStatus... statusesToMatch) {

		final Map<String, Relationship> allRelationships = userRelationships.get(userId);

		if (allRelationships == null) {
			return Collections.emptyList();
		}

		final List<RelationshipStatus> statusFilter = statusesToMatch == null ? Collections.emptyList()
				: Arrays.asList(statusesToMatch);

		return allRelationships.values().stream()
				.filter(relationship -> statusFilter.isEmpty() || statusFilter.contains(relationship.getStatus()))
				.collect(Collectors.toUnmodifiableList());
	}

	public void setRelationshipForUser(String userId, String otherUserId, RelationshipStatus status)
			throws InvalidRelationshipStateException {

		synchronized (RelationshipRepository.getRelationshipActionSynchronizationMonitor(userId, otherUserId)) {

			final Optional<Relationship> relationship = getCurrentRelationship(userId, otherUserId);
			final Relationship proposedRelationship = new Relationship(UUID.randomUUID().toString(), otherUserId,
					status, System.currentTimeMillis());
			final Relationship proposedRelationshipFlipped = RelationshipRepository.flip(userId, proposedRelationship);

			RelationshipRepository.validateStateChange(relationship, proposedRelationship);

			save(userId, proposedRelationship);
			save(otherUserId, proposedRelationshipFlipped);
		}
	}

	private Optional<Relationship> getCurrentRelationship(String userId, String otherUserId) {
		final Map<String, Relationship> relationships = userRelationships.get(userId);
		if (relationships != null) {
			return Optional.ofNullable(relationships.get(otherUserId));
		}
		return Optional.empty();
	}

	private static Relationship flip(String userId, Relationship relationship)
			throws InvalidRelationshipStateException {

		final RelationshipStatus flippedStatus;
		switch (relationship.getStatus()) {
		case CONNECTION_REQUEST_SENT:
			flippedStatus = RelationshipStatus.CONNECTION_REQUEST_RECEIVED;
			break;
		case CONNECTION_REJECTION_SENT:
			flippedStatus = RelationshipStatus.CONNECTION_REJECTION_RECEIVED;
			break;
		case CONNECTED:
			flippedStatus = RelationshipStatus.CONNECTED;
			break;
		case BLOCK_SENT:
			flippedStatus = RelationshipStatus.BLOCK_RECEIVED;
			break;
		default:
			throw new InvalidRelationshipStateException("Invalid proposed state: " + relationship.getStatus());
		}

		return new Relationship(UUID.randomUUID().toString(), userId, flippedStatus, relationship.getStatusSince());
	}

	private static void validateStateChange(Optional<Relationship> currentRelationship,
			Relationship proposedRelationship) throws InvalidRelationshipStateException {

		final RelationshipStatus currentStatus = currentRelationship.isPresent() ? currentRelationship.get().getStatus()
				: null;
		final RelationshipStatus proposedStatus = proposedRelationship.getStatus();

		switch (proposedStatus) {
		case CONNECTION_REQUEST_SENT:
			if (currentStatus == null || currentStatus == RelationshipStatus.CONNECTION_REJECTION_SENT
			|| currentStatus == RelationshipStatus.CONNECTION_REJECTION_RECEIVED) {
				return;
			}
			break;
		case CONNECTION_REJECTION_SENT:
			if (currentStatus == RelationshipStatus.CONNECTION_REQUEST_RECEIVED) {
				return;
			}
		case CONNECTED:
			if (currentStatus == RelationshipStatus.CONNECTION_REQUEST_RECEIVED
			|| currentStatus == RelationshipStatus.BLOCK_SENT) {
				return;
			}
			break;
		case BLOCK_SENT:
			return;
		default:
			break;
		}

		throw new InvalidRelationshipStateException(
				"Invalid proposal: change relationship status from " + currentStatus + " to " + proposedStatus);
	}

	private void save(String userId, Relationship relationship) {

		Map<String, Relationship> relationships = userRelationships.get(userId);
		if (relationships == null) {
			relationships = new HashMap<>();
			userRelationships.put(userId, relationships);
		}

		relationships.put(relationship.getUserId(), relationship);
	}

	private static String getRelationshipActionSynchronizationMonitor(String user1, String user2) {
		final String monitor = user1.compareTo(user2) < 0 ? user1 + user2 : user2 + user1;
		return monitor.intern();
	}

}
