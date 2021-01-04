package com.lisdoo.jstock.readwrite;

import java.util.Calendar;

public class Filter {

    /*
     * 跳过异常值，true正常，flase为异常
     */
    public static boolean check (String jstockCode, Data data) {

        // 代码不匹配的排除
        if (!jstockCode.equalsIgnoreCase(data.getCode())) return false;
        // 价格不对的排除
        if (data.getBuyPrice() == 0) return false;
        // 时间不对的排除
        Calendar last = Calendar.getInstance();
        last.setTime(data.getDateTime());
        if (last.get(Calendar.HOUR_OF_DAY) >= 9 && last.get(Calendar.HOUR_OF_DAY) <= 15) {
        } else {
            return false;
        }
        return true;
    }
}
