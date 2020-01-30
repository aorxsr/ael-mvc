package org.ael.exception;

/**
 * @Author: aorxsr
 * @Date: 2019/8/22 19:56
 */
public class NotFoundException extends Exception {

	public NotFoundException() {
		super();
	}

	public NotFoundException(String message) {
		super(message);
	}

	public NotFoundException(String message, Throwable cause) {
		super(message, cause);
	}

	public NotFoundException(Throwable cause) {
		super(cause);
	}
	
}
