package com.lisdoo.jstock.service.exchange.exception;

public class NotInRangeException extends Throwable implements JsException {
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

    @Override
    public Integer getCode() {
        return 9003;
    }
}
