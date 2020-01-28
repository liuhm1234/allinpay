package com.pack.iso8583.exception;

public class PacketException extends Exception {
	private static final long serialVersionUID = 1L;

	public PacketException() {
	}

	public PacketException(String message) {
		super(message);
	}
}