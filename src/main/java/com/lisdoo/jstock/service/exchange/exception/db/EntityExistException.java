package com.lisdoo.jstock.service.exchange.exception.db;

import com.lisdoo.jstock.service.exchange.exception.JsException;

public class EntityExistException extends Throwable implements JsException {
    public EntityExistException() {
    }

    public EntityExistException(String message) {
        super(message);
    }

    public EntityExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public EntityExistException(Throwable cause) {
        super(cause);
    }

    public EntityExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    @Override
    public Integer getCode() {
        return 9001;
    }
}
