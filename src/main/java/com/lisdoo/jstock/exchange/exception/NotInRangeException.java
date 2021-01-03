package com.lisdoo.jstock.exchange.exception;

public class NotInRangeException extends ExchangeException {
    public NotInRangeException() {
    }

    public NotInRangeException(String message) {
        super(message);
    }

    public NotInRangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotInRangeException(Throwable cause) {
        super(cause);
    }
}
