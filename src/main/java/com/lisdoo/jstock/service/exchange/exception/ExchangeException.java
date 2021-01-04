package com.lisdoo.jstock.service.exchange.exception;

public class ExchangeException extends Throwable implements JsException {
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

    @Override
    public Integer getCode() {
        return 9002;
    }
}
