package com.acintyo.exceptions;

@SuppressWarnings("serial")
public class DistributerAndFileTyepNotFoundException extends RuntimeException{
	
	public DistributerAndFileTyepNotFoundException() {
		super();
	}

    public DistributerAndFileTyepNotFoundException(String msg) {
    	super(msg);
    }
}
