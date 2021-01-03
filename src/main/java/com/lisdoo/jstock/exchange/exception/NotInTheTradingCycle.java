package com.lisdoo.jstock.exchange.exception;

public class NotInTheTradingCycle extends ExchangeException {
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
}
