package org.saharsh.gupchup.server.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RelationshipChange {

	@NotBlank(message = "Username required")
	private final String username;
	@NotNull(message = "New relationship status required")
	private final RelationshipStatus newStatus;

	@JsonCreator
	public RelationshipChange(@JsonProperty("username") String otherUsername,
			@JsonProperty("newStatus") RelationshipStatus newStatus) {
		super();
		this.username = otherUsername;
		this.newStatus = newStatus;
	}

	@JsonProperty("username")
	public String getUsername() {
		return username;
	}

	@JsonProperty("newStatus")
	public RelationshipStatus getNewStatus() {
		return newStatus;
	}

	@Override
	public String toString() {
		return "RelationshipChange [username=" + username + ", newStatus=" + newStatus + "]";
	}

}
