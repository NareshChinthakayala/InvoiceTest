package com.acintyo.exceptions;

public class InvalidNumericDataException extends RuntimeException{

	public InvalidNumericDataException() {
		super();
	}
	public InvalidNumericDataException(String msg) {
		super(msg);
	}
}
