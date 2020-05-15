package org.ael.route.exception;

public class NoRouteTypeException extends Exception {
    public NoRouteTypeException() {
        super();
    }

    public NoRouteTypeException(String mes, Throwable cause,
                                boolean str, boolean str1) {
        super(mes, cause, str, str1);
    }

    public NoRouteTypeException(String mes, Throwable cause) {
        super(mes, cause);
    }

    public NoRouteTypeException(String mes) {
        super(mes);
    }

    public NoRouteTypeException(Throwable cause) {
        super(cause);
    }
}
