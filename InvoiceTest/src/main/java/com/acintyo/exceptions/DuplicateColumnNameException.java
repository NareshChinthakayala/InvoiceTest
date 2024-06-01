package com.acintyo.exceptions;

@SuppressWarnings("serial")
public class DuplicateColumnNameException extends RuntimeException {

	public DuplicateColumnNameException() {
		super();
	}

	public DuplicateColumnNameException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DuplicateColumnNameException(String message, Throwable cause) {
		super(message, cause);
	}

	public DuplicateColumnNameException(String message) {
		super(message);
	}

	public DuplicateColumnNameException(Throwable cause) {
		super(cause);
	}

}
