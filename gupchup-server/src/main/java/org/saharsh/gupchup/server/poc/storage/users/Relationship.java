package org.saharsh.gupchup.server.poc.storage.users;

import org.saharsh.gupchup.server.model.RelationshipStatus;

public class Relationship {

	private final String id;
	private final String userId;
	private final RelationshipStatus status;
	private final long statusSince;

	public Relationship(String id, String userId, RelationshipStatus status, long statusSince) {
		super();
		this.id = id;
		this.userId = userId;
		this.status = status;
		this.statusSince = statusSince;
	}

	public String getId() {
		return id;
	}

	public String getUserId() {
		return userId;
	}

	public RelationshipStatus getStatus() {
		return status;
	}

	public long getStatusSince() {
		return statusSince;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Relationship) {
			return id.equals(((Relationship) obj).id);
		}

		return false;
	}

	@Override
	public String toString() {
		return "Relationship [id=" + id + ", userId=" + userId + ", status=" + status + ", statusSince=" + statusSince
				+ "]";
	}

}
