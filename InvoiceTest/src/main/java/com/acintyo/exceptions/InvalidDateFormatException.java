package com.acintyo.exceptions;

@SuppressWarnings("serial")
public class InvalidDateFormatException extends RuntimeException {

	public InvalidDateFormatException() {
		super();
	}

	public InvalidDateFormatException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public InvalidDateFormatException(String message, Throwable cause) {
		super(message, cause);
	}

	public InvalidDateFormatException(String message) {
		super(message);
	}

	public InvalidDateFormatException(Throwable cause) {
		super(cause);
	}

}
