package org.saharsh.gupchup.server.model;

import javax.validation.constraints.NotBlank;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DirectMessage extends Message {

	@NotBlank(message = "Receiver ID required")
	private final String receiverId;
	private final String content;

	@JsonCreator
	public DirectMessage(@JsonProperty("id") String id, @JsonProperty("senderId") String senderId,
			@JsonProperty("sentAt") Long sentAt, @JsonProperty("receiverId") String receiverId,
			@JsonProperty("content") String content) {
		super(id, senderId, sentAt);
		this.receiverId = receiverId;
		this.content = content;
	}

	@JsonProperty("receiverId")
	public String getReceiverId() {
		return receiverId;
	}

	@JsonProperty("content")
	public String getContent() {
		return content;
	}

	@Override
	public String toString() {
		return "DirectMessage [receiverId=" + receiverId + ", content=" + content + ", toString()=" + super.toString()
		+ "]";
	}
}
