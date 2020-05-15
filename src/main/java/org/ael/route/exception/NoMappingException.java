package org.ael.route.exception;

public class NoMappingException extends Exception {

    public NoMappingException() {
        super();
    }

    public NoMappingException(String mes, Throwable cause,
                              boolean str, boolean str1) {
        super(mes, cause, str, str1);
    }

    public NoMappingException(String mes, Throwable cause) {
        super(mes, cause);
    }

    public NoMappingException(String mes) {
        super(mes);
    }

    public NoMappingException(Throwable cause) {
        super(cause);
    }

}
