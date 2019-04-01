package org.saharsh.gupchup.server.resources;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.saharsh.gupchup.server.model.RelationshipChange;
import org.saharsh.gupchup.server.model.RelationshipStatus;
import org.saharsh.gupchup.server.model.User;
import org.saharsh.gupchup.server.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/users")
@RestController
public class Users {

	private final UserService service;

	@Autowired
	public Users(UserService service) {
		super();
		this.service = service;
	}

	@PostMapping
	public User create(@Valid @RequestBody User newUser) {
		return service.createUser(newUser);
	}

	@GetMapping(path = "{userId}/relationships")
	public Map<RelationshipStatus, List<User>> getRelatedUsers(@PathVariable("userId") String userId,
			@RequestParam(value = "status", required = false) RelationshipStatus[] statusesToMatch) {
		return service.getRelatedUsers(userId, statusesToMatch);
	}

	@PutMapping("{userId}/relationships")
	public void changeRelationship(@PathVariable("userId") String userId,
			@Valid @RequestBody RelationshipChange change) {
		service.changeRelationship(userId, new RelationshipChange(change.getUsername(), change.getNewStatus()));
	}

}
