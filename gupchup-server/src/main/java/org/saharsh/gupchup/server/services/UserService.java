package org.saharsh.gupchup.server.services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.saharsh.gupchup.server.model.RelationshipChange;
import org.saharsh.gupchup.server.model.RelationshipStatus;
import org.saharsh.gupchup.server.model.User;
import org.saharsh.gupchup.server.poc.storage.users.RelationshipRepository;
import org.saharsh.gupchup.server.poc.storage.users.UsersRepository;
import org.saharsh.gupchup.server.poc.storage.users.exceptions.InvalidRelationshipStateException;
import org.saharsh.gupchup.server.poc.storage.users.exceptions.UserExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {

	private final UsersRepository usersRepository;
	private final RelationshipRepository relationsRepository;

	@Autowired
	public UserService(UsersRepository usersRepository, RelationshipRepository relationsRepository) {
		super();
		this.usersRepository = usersRepository;
		this.relationsRepository = relationsRepository;
	}

	public User createUser(User newUser) {
		try {
			return usersRepository.createUser(newUser);
		} catch (final UserExistsException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
	}

	public Map<RelationshipStatus, List<User>> getRelatedUsers(String userId, RelationshipStatus... statusesToMatch) {

		final Map<RelationshipStatus, List<User>> relatedUsers = new HashMap<>();

		relationsRepository.getRelationshipsForUser(userId, statusesToMatch).forEach(relationship -> {

			final Optional<User> user = usersRepository.getUser(relationship.getUserId());
			if (user.isEmpty()) {
				return;
			}

			List<User> usersOfRelationship = relatedUsers.get(relationship.getStatus());

			if (usersOfRelationship == null) {
				usersOfRelationship = new ArrayList<>();
				relatedUsers.put(relationship.getStatus(), usersOfRelationship);
			}

			usersOfRelationship.add(user.get());
		});

		return relatedUsers;

	}

	public void changeRelationship(String requesterId, RelationshipChange change) {

		final String otherUsername = change.getUsername();
		final RelationshipStatus newStatus = change.getNewStatus();

		// validate users
		if (usersRepository.getUser(requesterId).isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user found for ID: " + requesterId);
		}
		final Optional<User> other = usersRepository.getUserByUsername(otherUsername);
		if (other.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No user found for username: " + otherUsername);
		}
		final String otherId = other.get().getId();

		// make the change
		try {
			relationsRepository.setRelationshipForUser(requesterId, otherId, newStatus);
		} catch (final InvalidRelationshipStateException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
		}
	}

}
