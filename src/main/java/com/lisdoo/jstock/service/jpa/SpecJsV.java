package com.lisdoo.jstock.service.jpa;

import java.util.Date;

public interface SpecJsV {

    String getCode();

    String getHref();

    String getJsStatus();

    Float getBasePrise();

    Integer getLastPosition();

    Date getLastTradeDate();

    String getRangeStatus();

    Float getPriceRange();

    Integer getCount();

    String getFre();

    String getStrategyHref();
}
