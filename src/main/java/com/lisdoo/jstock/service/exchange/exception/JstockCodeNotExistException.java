package com.lisdoo.jstock.service.exchange.exception;

public class JstockCodeNotExistException extends Throwable implements JsException {
    public JstockCodeNotExistException() {
    }

    public JstockCodeNotExistException(String message) {
        super(message);
    }

    public JstockCodeNotExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public JstockCodeNotExistException(Throwable cause) {
        super(cause);
    }

    @Override
    public Integer getCode() {
        return 9006;
    }
}
