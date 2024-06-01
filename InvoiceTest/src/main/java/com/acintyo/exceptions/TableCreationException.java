package com.acintyo.exceptions;

@SuppressWarnings("serial")
public class TableCreationException extends RuntimeException  {

	public TableCreationException() {
		super();
	}

	public TableCreationException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public TableCreationException(String message, Throwable cause) {
		super(message, cause);
	}

	public TableCreationException(String message) {
		super(message);
	}

	public TableCreationException(Throwable cause) {
		super(cause);
	}

	
	
}
