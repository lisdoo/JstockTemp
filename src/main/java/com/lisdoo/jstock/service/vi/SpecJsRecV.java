package com.lisdoo.jstock.service.vi;

import java.util.Date;

public interface SpecJsRecV {

    Integer getPosition();

    Float getPositionPrise();

    Float getPrice();

    String getStatus();

    Date getQuoteTime();

    Integer getVolume();

    Integer getAmount();

//    Integer getConformStatus();
}
