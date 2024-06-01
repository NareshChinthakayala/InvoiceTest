package com.acintyo.exceptions;

@SuppressWarnings("serial")
public class DistributerNotFoundException extends RuntimeException {

	public DistributerNotFoundException() {
		super();
	}

	public DistributerNotFoundException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public DistributerNotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public DistributerNotFoundException(String message) {
		super(message);
	}

	public DistributerNotFoundException(Throwable cause) {
		super(cause);
	}

}
