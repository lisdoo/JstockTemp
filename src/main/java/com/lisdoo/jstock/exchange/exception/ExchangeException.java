package com.lisdoo.jstock.exchange.exception;

public class ExchangeException extends Throwable {
    public ExchangeException() {
    }

    public ExchangeException(String message) {
        super(message);
    }

    public ExchangeException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExchangeException(Throwable cause) {
        super(cause);
    }
}
