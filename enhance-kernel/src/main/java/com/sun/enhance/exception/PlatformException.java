package com.sun.enhance.exception;

/**
 * Created by zksun on 16-1-7.
 */
public class PlatformException extends RuntimeException {

	public PlatformException() {
		super();
	}

	public PlatformException(String message) {
		super(message);
	}

	public PlatformException(String message, Throwable cause) {
		super(message, cause);
	}

	public PlatformException(Throwable cause) {
		super(cause);
	}

	protected PlatformException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
