package org.saharsh.gupchup.server.poc.storage.users.exceptions;

public class UserExistsException extends Exception {

	private static final long serialVersionUID = 1483129581961443250L;

	public UserExistsException(String message) {
		super(message);
	}

}
