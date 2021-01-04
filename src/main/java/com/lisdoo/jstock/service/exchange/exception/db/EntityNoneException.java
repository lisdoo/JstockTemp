package com.lisdoo.jstock.service.exchange.exception.db;

import com.lisdoo.jstock.service.exchange.exception.JsException;

public class EntityNoneException extends Throwable implements JsException {
    public EntityNoneException() {
    }

    public EntityNoneException(String message) {
        super(message);
    }

    public EntityNoneException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityNoneException(Throwable cause) {
        super(cause);
    }

    public EntityNoneException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public Integer getCode() {
        return 9005;
    }
}
