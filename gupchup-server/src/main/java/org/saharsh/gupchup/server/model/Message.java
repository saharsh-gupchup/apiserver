package org.saharsh.gupchup.server.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonTypeInfo(use = Id.NAME, property = "type")
@JsonSubTypes({ @Type(value = DirectMessage.class, name = "direct"),
	@Type(value = DeliveryConfirmation.class, name = "deliveryConfirmation") })
public abstract class Message {

	private final String id;
	@NotBlank(message = "Sender ID required")
	private final String senderId;
	@NotNull(message = "Time sent at in epoch millis required")
	private final Long sentAt;

	public Message(String id, String senderId, Long sentAt) {
		super();
		this.id = id;
		this.senderId = senderId;
		this.sentAt = sentAt;
	}

	@JsonProperty("id")
	public String getId() {
		return id;
	}

	@JsonProperty("senderId")
	public String getSenderId() {
		return senderId;
	}

	@JsonProperty("sentAt")
	public long getSentAt() {
		return sentAt;
	}

	@Override
	public boolean equals(Object obj) {

		if (obj instanceof Message) {
			return id.equals(((Message) obj).id);
		}

		return false;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public String toString() {
		return "Message [id=" + id + ", senderId=" + senderId + ", sentAt=" + sentAt + "]";
	}

}
