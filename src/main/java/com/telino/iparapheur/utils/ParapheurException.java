package com.telino.iparapheur.utils;

public class ParapheurException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8362510019219438228L;

	public ParapheurException() {
		super();
	}

	public ParapheurException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public ParapheurException(String message, Throwable cause) {
		super(message, cause);
	}

	public ParapheurException(String message) {
		super(message);
	}

	public ParapheurException(Throwable cause) {
		super(cause);
	}
}
