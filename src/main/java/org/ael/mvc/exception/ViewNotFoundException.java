package org.ael.mvc.exception;

/**
 * @Author: aorxsr
 * @Date: 2019/8/22 19:56
 */
public class ViewNotFoundException extends RuntimeException {

    public ViewNotFoundException() {
        super();
    }

    public ViewNotFoundException(String message) {
        super(message);
    }

    public ViewNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public ViewNotFoundException(Throwable cause) {
        super(cause);
    }

}
