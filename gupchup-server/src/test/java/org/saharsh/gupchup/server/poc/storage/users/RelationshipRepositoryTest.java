package org.saharsh.gupchup.server.poc.storage.users;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.saharsh.gupchup.server.model.RelationshipStatus;
import org.saharsh.gupchup.server.model.User;
import org.saharsh.gupchup.server.poc.storage.users.exceptions.InvalidRelationshipStateException;
import org.saharsh.gupchup.server.poc.storage.users.exceptions.UserExistsException;

public class RelationshipRepositoryTest {

	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	private UsersRepository userRepo = null;
	private RelationshipRepository repo = null;

	@Before
	public void setup() throws UserExistsException {
		userRepo = RelationshipRepositoryTest.createUserRepo();
		repo = new RelationshipRepository();
	}

	@Test
	public void test_setRelationshipForUser_for_initial_connection_request_happy_path() throws Exception {

		// setup
		final Date timeStart = new Date(System.currentTimeMillis() - 10L); // go back 10ms for sane comparisons later

		// run
		repo.setRelationshipForUser(id("user-one"), id("user-two"), RelationshipStatus.CONNECTION_REQUEST_SENT);

		// verify
		final Date timeEnd = new Date(System.currentTimeMillis() + 10L); // go forward 10ms for sane comparisons later

		final List<Relationship> userOneRelationships = repo.getRelationshipsForUser(id("user-one"));
		Assert.assertEquals(1, userOneRelationships.size());
		final Relationship oneToTwo = userOneRelationships.get(0);
		RelationshipRepositoryTest.validateRelationship(oneToTwo, id("user-two"),
				RelationshipStatus.CONNECTION_REQUEST_SENT, timeStart, timeEnd);

		final List<Relationship> userTwoRelationships = repo.getRelationshipsForUser(id("user-two"));
		Assert.assertEquals(1, userTwoRelationships.size());
		final Relationship twoToOne = userTwoRelationships.get(0);
		RelationshipRepositoryTest.validateRelationship(twoToOne, id("user-one"),
				RelationshipStatus.CONNECTION_REQUEST_RECEIVED, timeStart, timeEnd);
	}

	@Test
	public void test_setRelationshipForUser_for_request_to_connection_to_blocked() throws Exception {

		// connection request
		Date timeStart = new Date(System.currentTimeMillis() - 10L);
		repo.setRelationshipForUser(id("user-one"), id("user-two"), RelationshipStatus.CONNECTION_REQUEST_SENT);
		Date timeEnd = new Date(System.currentTimeMillis() + 10L);

		Relationship oneToTwo = repo.getRelationshipsForUser(id("user-one")).get(0);
		Relationship twoToOne = repo.getRelationshipsForUser(id("user-two")).get(0);
		RelationshipRepositoryTest.validateRelationship(oneToTwo, id("user-two"),
				RelationshipStatus.CONNECTION_REQUEST_SENT, timeStart, timeEnd);
		RelationshipRepositoryTest.validateRelationship(twoToOne, id("user-one"),
				RelationshipStatus.CONNECTION_REQUEST_RECEIVED, timeStart, timeEnd);

		// connection accept
		timeStart = new Date(System.currentTimeMillis() - 10L);
		repo.setRelationshipForUser(id("user-two"), id("user-one"), RelationshipStatus.CONNECTED);
		timeEnd = new Date(System.currentTimeMillis() + 10L);

		oneToTwo = repo.getRelationshipsForUser(id("user-one")).get(0);
		twoToOne = repo.getRelationshipsForUser(id("user-two")).get(0);
		RelationshipRepositoryTest.validateRelationship(oneToTwo, id("user-two"), RelationshipStatus.CONNECTED,
				timeStart, timeEnd);
		RelationshipRepositoryTest.validateRelationship(twoToOne, id("user-one"), RelationshipStatus.CONNECTED,
				timeStart, timeEnd);

		// blocked
		timeStart = new Date(System.currentTimeMillis() - 10L);
		repo.setRelationshipForUser(id("user-two"), id("user-one"), RelationshipStatus.BLOCK_SENT);
		timeEnd = new Date(System.currentTimeMillis() + 10L);

		oneToTwo = repo.getRelationshipsForUser(id("user-one")).get(0);
		twoToOne = repo.getRelationshipsForUser(id("user-two")).get(0);
		RelationshipRepositoryTest.validateRelationship(oneToTwo, id("user-two"), RelationshipStatus.BLOCK_RECEIVED,
				timeStart, timeEnd);
		RelationshipRepositoryTest.validateRelationship(twoToOne, id("user-one"), RelationshipStatus.BLOCK_SENT,
				timeStart, timeEnd);

		// unblock
		timeStart = new Date(System.currentTimeMillis() - 10L);
		repo.setRelationshipForUser(id("user-two"), id("user-one"), RelationshipStatus.CONNECTED);
		timeEnd = new Date(System.currentTimeMillis() + 10L);

		oneToTwo = repo.getRelationshipsForUser(id("user-one")).get(0);
		twoToOne = repo.getRelationshipsForUser(id("user-two")).get(0);
		RelationshipRepositoryTest.validateRelationship(oneToTwo, id("user-two"), RelationshipStatus.CONNECTED,
				timeStart, timeEnd);
		RelationshipRepositoryTest.validateRelationship(twoToOne, id("user-one"), RelationshipStatus.CONNECTED,
				timeStart, timeEnd);
	}

	@Test
	public void test_setRelationshipForUser_for_not_allowing_connection_without_request() throws Exception {

		// setup
		expectedException.expect(InvalidRelationshipStateException.class);
		expectedException.expectMessage(
				"Invalid proposal: change relationship status from " + null + " to " + RelationshipStatus.CONNECTED);

		// run and verify
		repo.setRelationshipForUser(id("user-one"), id("user-two"), RelationshipStatus.CONNECTED);
	}

	@Test
	public void test_setRelationshipForUser_does_not_let_connection_request_sender_accept() throws Exception {

		// setup
		repo.setRelationshipForUser(id("user-one"), id("user-two"), RelationshipStatus.CONNECTION_REQUEST_SENT);

		expectedException.expect(InvalidRelationshipStateException.class);
		expectedException.expectMessage("Invalid proposal: change relationship status from "
				+ RelationshipStatus.CONNECTION_REQUEST_SENT + " to " + RelationshipStatus.CONNECTED);

		// run and verify
		repo.setRelationshipForUser(id("user-one"), id("user-two"), RelationshipStatus.CONNECTED);
	}

	@Test
	public void test_setRelationshipForUser_for_not_accepting_connection_request_when_connected() throws Exception {

		// setup
		repo.setRelationshipForUser(id("user-one"), id("user-two"), RelationshipStatus.CONNECTION_REQUEST_SENT);
		repo.setRelationshipForUser(id("user-two"), id("user-one"), RelationshipStatus.CONNECTED);

		expectedException.expect(InvalidRelationshipStateException.class);
		expectedException.expectMessage("Invalid proposal: change relationship status from "
				+ RelationshipStatus.CONNECTED + " to " + RelationshipStatus.CONNECTION_REQUEST_SENT);

		// run and verify
		repo.setRelationshipForUser(id("user-one"), id("user-two"), RelationshipStatus.CONNECTION_REQUEST_SENT);
	}

	@Test
	public void test_setRelationshipForUser_for_not_accepting_connection_received() throws Exception {

		// setup
		expectedException.expect(InvalidRelationshipStateException.class);
		expectedException.expectMessage("Invalid proposed state: " + RelationshipStatus.CONNECTION_REQUEST_RECEIVED);

		// run and verify
		repo.setRelationshipForUser(id("user-one"), id("user-two"), RelationshipStatus.CONNECTION_REQUEST_RECEIVED);
	}

	@Test
	public void test_setRelationshipForUser_does_not_let_blocked_party_unblock() throws Exception {

		// setup
		repo.setRelationshipForUser(id("user-one"), id("user-two"), RelationshipStatus.CONNECTION_REQUEST_SENT);
		repo.setRelationshipForUser(id("user-two"), id("user-one"), RelationshipStatus.CONNECTED);
		repo.setRelationshipForUser(id("user-one"), id("user-two"), RelationshipStatus.BLOCK_SENT);

		expectedException.expect(InvalidRelationshipStateException.class);
		expectedException.expectMessage("Invalid proposal: change relationship status from "
				+ RelationshipStatus.BLOCK_RECEIVED + " to " + RelationshipStatus.CONNECTED);

		// run and verify
		repo.setRelationshipForUser(id("user-two"), id("user-one"), RelationshipStatus.CONNECTED);
	}

	@Test
	public void test_setRelationshipForUser_for_not_accepting_block_received() throws Exception {

		// setup
		repo.setRelationshipForUser(id("user-one"), id("user-two"), RelationshipStatus.CONNECTION_REQUEST_SENT);
		repo.setRelationshipForUser(id("user-two"), id("user-one"), RelationshipStatus.CONNECTED);

		expectedException.expect(InvalidRelationshipStateException.class);
		expectedException.expectMessage("Invalid proposed state: " + RelationshipStatus.BLOCK_RECEIVED);

		// run and verify
		repo.setRelationshipForUser(id("user-one"), id("user-two"), RelationshipStatus.BLOCK_RECEIVED);
	}

	private String id(String username) {
		return userRepo.getUserByUsername(username).get().getId();
	}

	private static void validateRelationship(Relationship relationship, String expectedUserId,
			RelationshipStatus expectedStatus, Date createdAfter, Date createdBefore) {
		Assert.assertEquals(expectedUserId, relationship.getUserId());
		Assert.assertEquals(expectedStatus, relationship.getStatus());
		Assert.assertNotNull(relationship.getId());

		final Date timeCreated = new Date(relationship.getStatusSince());
		Assert.assertTrue(createdAfter.before(timeCreated));
		Assert.assertTrue(createdBefore.after(timeCreated));
	}

	private static UsersRepository createUserRepo() throws UserExistsException {
		final UsersRepository repo = new UsersRepository();

		repo.createUser(new User(null, "user-one", "user one"));
		repo.createUser(new User(null, "user-two", "user two"));
		repo.createUser(new User(null, "user-three", "user three"));
		repo.createUser(new User(null, "user-four", "user four"));
		repo.createUser(new User(null, "user-five", "user five"));

		return repo;
	}

}
