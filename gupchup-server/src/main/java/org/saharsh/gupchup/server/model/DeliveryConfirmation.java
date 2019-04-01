package org.saharsh.gupchup.server.model;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class DeliveryConfirmation extends Message {

	@NotEmpty(message = "Must contain at least one message ID")
	private final Set<String> messageIdsDelivered;

	@JsonCreator
	public DeliveryConfirmation(@JsonProperty("id") String id, @JsonProperty("senderId") String senderId,
			@JsonProperty("sentAt") Long sentAt, @JsonProperty("messageIdsDelivered") String... messageIdsDelivered) {
		super(id, senderId, sentAt);
		this.messageIdsDelivered = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(messageIdsDelivered)));
	}

	@JsonProperty("messageIdsDelivered")
	public Set<String> getMessageIdsDelivered() {
		return messageIdsDelivered;
	}

	@Override
	public String toString() {
		return "DeliveryConfirmation [messageIdsDelivered=" + messageIdsDelivered + ", toString()=" + super.toString()
		+ "]";
	}
}
