package org.saharsh.gupchup.server.model;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class User {

	private final String id;
	@NotBlank(message = "Username required")
	private final String username;
	@NotBlank(message = "Display name required")
	private final String displayName;

	@JsonCreator
	public User(@JsonProperty("id") String id, @JsonProperty("username") String username,
			@JsonProperty("displayName") String displayName) {
		super();
		this.id = id;
		this.username = username;
		this.displayName = displayName;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("username")
	public String getUsername() {
		return username;
	}

	@JsonProperty("displayName")
	public String getDisplayName() {
		return displayName;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof User) {
			return id.equals(((User) obj).id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", displayName=" + displayName + "]";
	}
}
