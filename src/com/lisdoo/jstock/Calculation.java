package com.lisdoo.jstock;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class Calculation {

    public enum Frequency {HOUR, DAY, WEEK, MONTH, YEAR};

    /*
     * current price
     */
    public static Float getCurrentPrice(Integer totle, Integer volume) {
        return totle.floatValue()/volume;
    }

    /*
     * get range
     */
    public static Map<Integer, Float> createRangeValue(Float currentPrice, Float range, Integer count, Float offset, Frequency fre, Date lastTrans, Date cur) {

        Calendar last = Calendar.getInstance();
        last.setTime(lastTrans);
        Calendar current = Calendar.getInstance();
        if (cur != null) current.setTime(cur);

//        System.out.println(last.getTime());
//        System.out.println(current.getTime());

        boolean is = false;
        switch(fre) {
            case HOUR: {
                if (current.get(Calendar.YEAR) > last.get(Calendar.YEAR)) {
                    is = true;
                } else if (current.get(Calendar.YEAR) == last.get(Calendar.YEAR)) {
                    if (current.get(Calendar.MONTH) > last.get(Calendar.MONTH)) {
                        is = true;
                    } else if (current.get(Calendar.MONTH) == last.get(Calendar.MONTH)) {
                        if (current.get(Calendar.DAY_OF_MONTH) > last.get(Calendar.DAY_OF_MONTH)) {
                            is = true;
                        } else if (current.get(Calendar.DAY_OF_MONTH) == last.get(Calendar.DAY_OF_MONTH)) {
                            if (current.get(Calendar.HOUR_OF_DAY) > last.get(Calendar.HOUR_OF_DAY)) {
                                is = true;
                            }
                        }
                    }
                }
            } break;
            case DAY: {
                if (current.get(Calendar.YEAR) > last.get(Calendar.YEAR)) {
                    is = true;
                } else if (current.get(Calendar.YEAR) == last.get(Calendar.YEAR)) {
                    if (current.get(Calendar.MONTH) > last.get(Calendar.MONTH)) {
                        is = true;
                    } else if (current.get(Calendar.MONTH) == last.get(Calendar.MONTH)) {
                        if (current.get(Calendar.DAY_OF_MONTH) > last.get(Calendar.DAY_OF_MONTH)) {
                            is = true;
                        }
                    }
                }
            } break;
            case WEEK: {
                if (current.get(Calendar.YEAR) > last.get(Calendar.YEAR)) {
                    is = true;
                } else if (current.get(Calendar.YEAR) == last.get(Calendar.YEAR)) {
                    if (current.get(Calendar.MONTH) > last.get(Calendar.MONTH)) {
                        is = true;
                    } else if (current.get(Calendar.MONTH) == last.get(Calendar.MONTH)) {
                        if (current.get(Calendar.WEEK_OF_MONTH) > last.get(Calendar.WEEK_OF_MONTH)) {
                            is = true;
                        }
                    }
                }
            } break;
            case MONTH: {
                if (current.get(Calendar.YEAR) > last.get(Calendar.YEAR)) {
                    is = true;
                } else if (current.get(Calendar.YEAR) == last.get(Calendar.YEAR)) {
                    if (current.get(Calendar.MONTH) > last.get(Calendar.MONTH)) {
                        is = true;
                    }
                }
            } break;
            case YEAR: {
                if (current.get(Calendar.YEAR) > last.get(Calendar.YEAR)) {
                    is = true;
                }
            } break;
            default: {
                System.out.println("Unknow Frequency");
            }
        }
        System.out.println(Frequency.HOUR.toString() + " " + is);

        if (is) {

        }

        return null;
    }

    /*
     * get new price
     */
    public static Map<Integer, Float> getNewPrice(Float realTimePrice, Map<Integer, Float> rangePrice) {

        return null;
    }

    /*
     * get new volume
     */
    public static Integer getNewVolume(Map<Integer, Float> newPrice, Integer oriVolume) {

        return null;
    }

    /*
     * get new totle
     */
    public static void getNewTotle(Map<Integer, Float> newPrice, Integer oriVolume) {

    }

    public static void main(String[] args) {
        for (float i = 1; i<10; i++) {
            System.out.println( i/10 );
            System.out.println(Math.log( i/10 ));
        }
    }

    public static void createRangeValueTimeTest() {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(2020,12,12,12,12,12);

        Calendar lastTransCalendar = Calendar.getInstance();
        lastTransCalendar.set(2020,12,12,11,12,12);
        createRangeValue(0f,0f,0,0f, Frequency.HOUR, lastTransCalendar.getTime(), currentCalendar.getTime());
        lastTransCalendar.set(2020,12,11,17,20,20);
        createRangeValue(0f,0f,0,0f, Frequency.DAY, lastTransCalendar.getTime(), currentCalendar.getTime());
        lastTransCalendar.set(2020,12,5,17,20,20);
        createRangeValue(0f,0f,0,0f, Frequency.WEEK, lastTransCalendar.getTime(), currentCalendar.getTime());
        lastTransCalendar.set(2020,11,28,17,20,20);
        createRangeValue(0f,0f,0,0f, Frequency.MONTH, lastTransCalendar.getTime(), currentCalendar.getTime());
        lastTransCalendar.set(2019,11,28,17,20,20);
        createRangeValue(0f,0f,0,0f, Frequency.YEAR, lastTransCalendar.getTime(), currentCalendar.getTime());
    }

    public static void getCurrentPriceTest() {

        Integer i = 0;
        Integer j = 0;

        i = 100; j=10;
        System.out.println(String.format("除法测试：%d / %d = %f",  i, j, Calculation.getCurrentPrice(i, j)));
        System.out.println(Calculation.getCurrentPrice(i, j) == 10f);
        i = 123; j=14;
        System.out.println(String.format("除法测试：%d / %d = %f",  i, j, Calculation.getCurrentPrice(i, j)));
        System.out.println(Calculation.getCurrentPrice(i, j) == 8.785714f);
        i = 123142; j=321;
        System.out.println(String.format("除法测试：%d / %d = %f",  i, j, Calculation.getCurrentPrice(i, j)));
        System.out.println(Calculation.getCurrentPrice(i, j) == 383.619937f);
        i = 5423443; j=123;
        System.out.println(String.format("除法测试：%d / %d = %f",  i, j, Calculation.getCurrentPrice(i, j)));
        System.out.println(Calculation.getCurrentPrice(i, j) == 44093.032520f);
        i = 5432; j=532;
        System.out.println(String.format("除法测试：%d / %d = %f",  i, j, Calculation.getCurrentPrice(i, j)));
        System.out.println(Calculation.getCurrentPrice(i, j) == 10.210526f);
        i = 123; j=45324;
        System.out.println(String.format("除法测试：%d / %d = %f",  i, j, Calculation.getCurrentPrice(i, j)));
        System.out.println(Calculation.getCurrentPrice(i, j) == 0.0027137941f);
        i = 123; j=132;
        System.out.println(String.format("除法测试：%d / %d = %f",  i, j, Calculation.getCurrentPrice(i, j)));
        System.out.println(Calculation.getCurrentPrice(i, j) == 0.9318181818f);
        i = 12; j=54345;
        System.out.println(String.format("除法测试：%d / %d = %f",  i, j, Calculation.getCurrentPrice(i, j)));
        System.out.println(Calculation.getCurrentPrice(i, j) == 2.2081149E-4f);
        i = 10; j=0;
        System.out.println(String.format("除法测试：%d / %d = %f",  i, j, Calculation.getCurrentPrice(i, j)));
        System.out.println(Calculation.getCurrentPrice(i, j) == Float.POSITIVE_INFINITY);
        i = 0; j=10;
        System.out.println(String.format("除法测试：%d / %d = %f",  i, j, Calculation.getCurrentPrice(i, j)));
        System.out.println(Calculation.getCurrentPrice(i, j) == 0f);
    }
}
