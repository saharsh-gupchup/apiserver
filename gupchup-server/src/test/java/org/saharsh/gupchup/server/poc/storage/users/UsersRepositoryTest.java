package org.saharsh.gupchup.server.poc.storage.users;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.saharsh.gupchup.server.model.User;
import org.saharsh.gupchup.server.poc.storage.users.exceptions.UserExistsException;

public class UsersRepositoryTest {

	@Test
	public void test_createUser() throws UserExistsException {

		// setup
		final UsersRepository repo = new UsersRepository();

		// run
		final User created = repo.createUser(new User(null, "some-user", "Some User"));

		// verify
		Assert.assertNotNull(created);
		Assert.assertNotNull(created.getId());
		Assert.assertEquals("some-user", created.getUsername());
		Assert.assertEquals("Some User", created.getDisplayName());
	}

	@Test
	public void test_createUser_overwrites_id() throws UserExistsException {

		// setup
		final UsersRepository repo = new UsersRepository();

		// run
		final User created = repo.createUser(new User("whatever", "some-user", "Some User"));

		// verify
		Assert.assertNotNull(created);
		Assert.assertNotNull(created.getId());
		Assert.assertNotEquals("whatever", created.getId());
		Assert.assertEquals("some-user", created.getUsername());
		Assert.assertEquals("Some User", created.getDisplayName());
	}

	@Test(expected = UserExistsException.class)
	public void test_createUser_fails_if_username_exists() throws UserExistsException {

		// setup
		final UsersRepository repo = new UsersRepository();

		// run
		repo.createUser(new User(null, "some-user", "Some User"));
		repo.createUser(new User(null, "some-user", "Some User"));
	}

	@Test
	public void test_isUsernameAvailable_when_username_is_available() throws UserExistsException {

		// setup
		final UsersRepository repo = new UsersRepository();

		// run and verify
		Assert.assertTrue(repo.isUsernameAvailable("some-user"));
	}

	@Test
	public void test_isUsernameAvailable_when_username_is_not_available() throws UserExistsException {

		// setup
		final UsersRepository repo = new UsersRepository();
		repo.createUser(new User(null, "some-user", "Some User"));

		// run and verify
		Assert.assertFalse(repo.isUsernameAvailable("some-user"));
	}

	@Test
	public void test_getUserByUsername_when_user_does_not_exist() throws UserExistsException {

		// setup
		final UsersRepository repo = new UsersRepository();

		// run
		final Optional<User> retrieved = repo.getUserByUsername("some-user");

		// verify
		Assert.assertTrue(retrieved.isEmpty());
	}

	@Test
	public void test_getUserByUsername_when_user_exists() throws UserExistsException {

		// setup
		final UsersRepository repo = new UsersRepository();
		repo.createUser(new User(null, "some-user", "Some User"));

		// run
		final Optional<User> retrieved = repo.getUserByUsername("some-user");

		// verify
		Assert.assertTrue(retrieved.isPresent());
		final User user = retrieved.get();
		Assert.assertNotNull(user.getId());
		Assert.assertEquals("some-user", user.getUsername());
		Assert.assertEquals("Some User", user.getDisplayName());
	}

	@Test
	public void test_getUsers() throws UserExistsException {

		// setup
		final UsersRepository repo = new UsersRepository();
		final User userOne = repo.createUser(new User(null, "user-one", "User One"));
		final User userTwo = repo.createUser(new User(null, "user-two", "User Two"));
		final User userThree = repo.createUser(new User(null, "user-three", "User Three"));
		final User userFour = repo.createUser(new User(null, "user-four", "User Four"));

		// run
		final List<User> retrievedUsers = repo.getUsers(Set.of(userOne.getId(), userThree.getId()));

		// verify
		Assert.assertEquals(2, retrievedUsers.size());
		Assert.assertTrue(retrievedUsers.contains(userOne));
		Assert.assertFalse(retrievedUsers.contains(userTwo));
		Assert.assertTrue(retrievedUsers.contains(userThree));
		Assert.assertFalse(retrievedUsers.contains(userFour));
	}

	@Test
	public void test_getUsers_when_no_users_in_set() throws UserExistsException {

		// setup
		final UsersRepository repo = new UsersRepository();
		repo.createUser(new User(null, "user-one", "User One"));
		repo.createUser(new User(null, "user-two", "User Two"));
		repo.createUser(new User(null, "user-three", "User Three"));
		repo.createUser(new User(null, "user-four", "User Four"));

		// run
		final List<User> retrievedUsers = repo.getUsers(Collections.emptySet());

		// verify
		Assert.assertEquals(0, retrievedUsers.size());
	}

}
