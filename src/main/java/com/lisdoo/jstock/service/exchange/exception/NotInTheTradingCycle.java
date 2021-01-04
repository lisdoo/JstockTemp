package com.lisdoo.jstock.service.exchange.exception;

public class NotInTheTradingCycle extends Throwable implements JsException {
    public NotInTheTradingCycle() {
    }

    public NotInTheTradingCycle(String message) {
        super(message);
    }

    public NotInTheTradingCycle(String message, Throwable cause) {
        super(message, cause);
    }

    public NotInTheTradingCycle(Throwable cause) {
        super(cause);
    }

    @Override
    public Integer getCode() {
        return 9004;
    }
}
