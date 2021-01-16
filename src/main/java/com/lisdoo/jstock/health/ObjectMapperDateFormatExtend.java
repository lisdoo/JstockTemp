package com.lisdoo.jstock.health;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ObjectMapperDateFormatExtend extends DateFormat {

    private static final long serialVersionUID = 1L;
    private DateFormat dateFormat;

    private SimpleDateFormat format1 = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");

    public ObjectMapperDateFormatExtend(DateFormat dateFormat) {//构造函数传入objectmapper默认的dateformat
        this.dateFormat = dateFormat;
    }
    //序列化时会执行这个方法
    @Override
    public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition fieldPosition) {
        return dateFormat.format(date,toAppendTo,fieldPosition);
    }
    //反序列化时执行此方法，我们先让他执行我们自己的format，如果异常则执执行他的
    //当然这里只是简单实现，可以有更优雅的方式来处理更多的格式
    @Override
    public Date parse(String source, ParsePosition pos) {
        Date date;

        try {
            date = format1.parse(source, pos);
        } catch (Exception e) {
            date = dateFormat.parse(source, pos);
        }
        return date;
    }
    //此方法在objectmapper 默认的dateformat里边用到，这里也要重写
    @Override
    public Object clone() {
        DateFormat dateFormat = (DateFormat) this.dateFormat.clone();
        return new ObjectMapperDateFormatExtend(dateFormat);
    }
}
