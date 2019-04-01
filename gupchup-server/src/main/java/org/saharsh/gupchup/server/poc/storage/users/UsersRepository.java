package org.saharsh.gupchup.server.poc.storage.users;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.saharsh.gupchup.server.model.User;
import org.saharsh.gupchup.server.poc.storage.users.exceptions.UserExistsException;

public class UsersRepository {

	private final Map<String, String> usernameToIdMap = new HashMap<>();
	private final Map<String, User> users = new HashMap<>();

	public boolean isUsernameAvailable(String username) {
		return usernameToIdMap.get(username) == null;
	}

	public Optional<User> getUser(String id) {
		return Optional.ofNullable(users.get(id));
	}

	public Optional<User> getUserByUsername(String username) {
		return getUser(usernameToIdMap.get(username));
	}

	public List<User> getUsers(Set<String> ids) {
		return users.entrySet().stream().filter(idUserPair -> ids.contains(idUserPair.getKey()))
				.map(idUserPair -> idUserPair.getValue()).collect(Collectors.toUnmodifiableList());
	}

	public User createUser(User user) throws UserExistsException {

		synchronized (user.getUsername().intern()) {

			if (usernameToIdMap.get(user.getUsername()) != null) {
				throw new UserExistsException("Username '" + user.getUsername() + "' not available");
			}

			user = new User(UUID.randomUUID().toString(), user.getUsername(), user.getDisplayName());
			usernameToIdMap.put(user.getUsername(), user.getId());
			users.put(user.getId(), user);
			return user;

		}
	}

}
