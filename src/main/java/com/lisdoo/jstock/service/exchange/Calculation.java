package com.lisdoo.jstock.service.exchange;

import com.lisdoo.jstock.service.exchange.exception.NotInRangeException;
import com.lisdoo.jstock.service.exchange.exception.NotInTheTradingCycle;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.*;

public class Calculation {

    public enum Frequency {HOUR, DAY, WEEK, MONTH, YEAR, ALWAYS};
    public enum Status {SELL, BUY};
    static DecimalFormat decimalFormat=new DecimalFormat("#.##");

    /*
     * current price
     */
    public static Float getCurrentPrice(Integer totle, Integer volume) {
        return totle.floatValue()/volume;
    }

    /*
     * get range
     */
    public static Map<Integer, Float> createRangeValue(Float basePrice, Float priceRange, Integer count, Float offset) {

//        System.out.println(String.format("currentPrice:%.2f priceRange:%.2f%% count:%d offset:%.2f", basePrice, priceRange*100, count, offset));

        Map<Integer, Float> prices = new TreeMap<>();

        float j = 0f;
        for (Integer i = 1; i<=count; i++) {
            j += Math.pow( i , offset);
            prices.put(i, Double.valueOf(Math.pow( i , offset)).floatValue());
        }
        Float magnification = prices.get(count)/j/priceRange;
        for (Integer i = 1; i<=count; i++) {
            Float proportion = Double.valueOf(Math.pow( i , offset) / j / magnification).floatValue();
            prices.put(i, proportion);
            prices.put(-i, -proportion);
        }

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<Integer, Float> entry: prices.entrySet()) {
            sb.insert(0, String.format("% 2d | % 6.2f%% | % 6.2f \r\n", entry.getKey(), entry.getValue()*100, basePrice*(1+entry.getValue())));
            entry.setValue(Float.valueOf(decimalFormat.format(basePrice*(1+entry.getValue()))));
        }
//        System.out.println(sb.toString());

        return prices;
    }
    public static Map<Integer, Float> createRangeValue2(Float basePrice, Float priceRange, Integer count, Float offset) {

        System.out.println(String.format("currentPrice:%.2f priceRange:%.2f%% count:%d offset:%.2f", basePrice, priceRange*100, count, offset));

        Map<Integer, Float> prices = new TreeMap<>();

        float j = 0f;
        for (Integer i = 1; i<=count; i++) {
            j += Math.pow( i , offset);
            prices.put(i, Double.valueOf(Math.pow( i , offset)).floatValue());
        }
        Float magnification = prices.get(count)/j/priceRange;
        for (Integer i = 1; i<=count; i++) {
            Float proportion = Double.valueOf(Math.pow( i , offset) / j / magnification).floatValue();
            prices.put(i, proportion);
            prices.put(-i, -proportion);
        }

        StringBuffer sb = new StringBuffer();
        for (Map.Entry<Integer, Float> entry: prices.entrySet()) {
            sb.insert(0, String.format("% 2d | % 6.2f%% | % 6.2f \r\n", entry.getKey(), entry.getValue()*100, basePrice*(1+entry.getValue())));
            entry.setValue(Float.valueOf(decimalFormat.format(basePrice*(1+entry.getValue()))));
        }
        System.out.println(sb.toString());

        return prices;
    }

    /*
     * get range position
     */
    public static Map.Entry<Integer, Float> getRangePosition(Float currentPrice, Map<Integer, Float> range, Frequency fre, Date lastTrans, Date cur) throws NotInRangeException, NotInTheTradingCycle {

        Calendar last = Calendar.getInstance();
        if (lastTrans == null) {
            last.setTimeInMillis(0);
        } else {
            last.setTime(lastTrans);
        }
        Calendar current = Calendar.getInstance();
        if (cur != null) current.setTime(cur);

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
            case ALWAYS: {
                is = true;
            } break;
            default: {
                System.out.println("Unknow Frequency");
            }
        }

//        System.out.println("need trigger " + is);

        if (is) {

            if (range == null) return null;

            Float max = Collections.max(range.values());
            Float min = Collections.min(range.values());

            if (currentPrice>min && currentPrice<max) {
                // 比较前对Map中值进行排序
                List<Map.Entry<Integer,Float>> list = new ArrayList<Map.Entry<Integer,Float>>(range.entrySet());
                Collections.sort(list,new Comparator<Map.Entry<Integer,Float>>() {
                    public int compare(Map.Entry<Integer, Float> o1,
                                       Map.Entry<Integer, Float> o2) {
                        Float f = ((Float)((Math.abs(currentPrice-o2.getValue()) - Math.abs(currentPrice-o1.getValue()))));
                        Integer i = ((Float)(f*1000000)).intValue();
                        return i;
                    }
                });
                // 找出绝对值最小的
                Stack<Map.Entry<Integer, Float>> stack = new Stack<>();
                for (Map.Entry<Integer, Float> entry: list) {
                    Float temp = Math.abs(currentPrice-entry.getValue());
                    if (stack.isEmpty()) stack.push(entry);
                    if (entry!=stack.peek() && Math.abs(currentPrice-stack.peek().getValue()) >= temp) {
                        stack.push(entry);
                    }
                }
                // 当Key和Value正负相同时，反馈
                // 当Key和Value正负不同时，取倒数第二个，若正负也不同，即未找到
                if (stack.peek().getKey() > 0 && currentPrice-stack.peek().getValue()>=0) {
                    return stack.peek();
                } else if (stack.peek().getKey() < 0 && currentPrice-stack.peek().getValue()<=0) {
                    return stack.peek();
                } else {
                    stack.pop();
                    if (stack.isEmpty()) throw new NotInRangeException("none position");
                    if (stack.peek().getKey() > 0 && currentPrice-stack.peek().getValue()>=0) {
                        return stack.peek();
                    } else if (stack.peek().getKey() < 0 && currentPrice-stack.peek().getValue()<=0) {
                        return stack.peek();
                    } else {
                        Map.Entry<Integer, Float> entry = new Map.Entry<Integer, Float>() {

                            Float value;

                            @Override
                            public Integer getKey() {
                                return 0;
                            }

                            @Override
                            public Float getValue() {
                                return this.value;
                            }

                            @Override
                            public Float setValue(Float value) {
                                this.value = value;
                                return this.value;
                            }
                        };
                        entry.setValue(currentPrice);
                        return entry;
                    }
                }

            } else {
                throw new NotInRangeException(String.format("Current: %.2f  max: %.2f  min: %.2f", currentPrice, max, min));
            }
        } else {
            throw new NotInTheTradingCycle();
        }
    }

    /*
     * get sange strategy
     */
    public static void getRangePosition() {

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

    public static void main(String[] args) throws NotInRangeException, Exception, NotInTheTradingCycle {

//        createRangeValueTest();
        createRangeValueFromDBTest();
//        getRangeStrategyTest();
//        getRangeStrategyTest2();
    }

    public static void createRangeValueTest() throws IOException {

        createRangeValue(10f, 0.1f,5, 0.5f);
        createRangeValue(10f, 0.1f,5, 1f);
        createRangeValue(10f, 0.1f,5, 1.5f);
        createRangeValue(10f, 0.1f,5, 2f);
    }

    public static void createRangeValueFromDBTest() throws Exception {


        //1、导入驱动jar包
        //2、注册驱动
        Class.forName("com.mysql.jdbc.Driver");

        //3、获取数据库的连接对象
        Connection con = DriverManager.getConnection("jdbc:mysql://ali47:3306/jstock", "root", "lenovo.112");

        //4、定义sql语句
        String sql = "select jr.id , jr.base_prise , js.count, js.offset , js.price_range , js.ratio from jstock_range jr left join jstock_strategy js on jr.jstock_strategy_id = js.id where jr.jstock_id is not null;";

        //5、获取执行sql语句的对象
        Statement stat = con.createStatement();

        //6、执行sql并接收返回结果
        ResultSet rs = stat.executeQuery(sql);

        //7、处理结果
        while (rs.next()) {

            if (rs.getInt("jr.id") == 4) {
                createRangeValue2(rs.getFloat("jr.base_prise"), rs.getFloat("js.price_range"), rs.getInt("js.count"), rs.getFloat("js.offset"));
            }
        }


        //8、释放资源
        stat.close();
        con.close();
    }

    public static void getRangeStrategyTest() throws NotInRangeException, NotInTheTradingCycle {
        Calendar currentCalendar = Calendar.getInstance();
        currentCalendar.set(2020,12,12,12,12,12);

        Calendar lastTransCalendar = Calendar.getInstance();
        lastTransCalendar.set(2020,12,12,11,12,12);
        getRangePosition(0f,null, Frequency.HOUR, lastTransCalendar.getTime(), currentCalendar.getTime());
        lastTransCalendar.set(2020,12,11,17,20,20);
        getRangePosition(0f,null, Frequency.DAY, lastTransCalendar.getTime(), currentCalendar.getTime());
        lastTransCalendar.set(2020,12,5,17,20,20);
        getRangePosition(0f,null, Frequency.WEEK, lastTransCalendar.getTime(), currentCalendar.getTime());
        lastTransCalendar.set(2020,11,28,17,20,20);
        getRangePosition(0f,null, Frequency.MONTH, lastTransCalendar.getTime(), currentCalendar.getTime());
        lastTransCalendar.set(2019,11,28,17,20,20);
        getRangePosition(0f,null, Frequency.YEAR, lastTransCalendar.getTime(), currentCalendar.getTime());
        lastTransCalendar.set(2019,11,28,17,20,20);
        getRangePosition(0f,null, Frequency.YEAR, null, currentCalendar.getTime());
    }

    public static void getRangeStrategyTest2() throws NotInRangeException, NotInTheTradingCycle {

        Calendar currentCalendar = Calendar.getInstance();
        Map<Integer, Float> prices = new TreeMap<>();
        prices.put(5, 11.0f);
        prices.put(4, 10.8f);
        prices.put(3, 10.6f);
        prices.put(2, 10.4f);
        prices.put(1, 10.2f);
        prices.put(-1, 9.8f);
        prices.put(-2, 9.6f);
        prices.put(-3, 9.4f);
        prices.put(-4, 9.3f);
        prices.put(-5, 9.0f);
        Map.Entry<Integer, Float> entry = null;
        entry = getRangePosition(10.71f,prices, Frequency.ALWAYS, currentCalendar.getTime(), currentCalendar.getTime());
        System.out.println(entry == null? null: String.format("key:%d value:%.2f", entry.getKey(), entry.getValue()));
        entry = getRangePosition(10.69f,prices, Frequency.ALWAYS, currentCalendar.getTime(), currentCalendar.getTime());
        System.out.println(entry == null? null: String.format("key:%d value:%.2f", entry.getKey(), entry.getValue()));
        entry = getRangePosition(10.5f,prices, Frequency.ALWAYS, currentCalendar.getTime(), currentCalendar.getTime());
        System.out.println(entry == null? null: String.format("key:%d value:%.2f", entry.getKey(), entry.getValue()));
        entry = getRangePosition(9.8f,prices, Frequency.ALWAYS, currentCalendar.getTime(), currentCalendar.getTime());
        System.out.println(entry == null? null: String.format("key:%d value:%.2f", entry.getKey(), entry.getValue()));
        entry = getRangePosition(9.49f,prices, Frequency.ALWAYS, currentCalendar.getTime(), currentCalendar.getTime());
        System.out.println(entry == null? null: String.format("key:%d value:%.2f", entry.getKey(), entry.getValue()));
        entry = getRangePosition(10.61f,prices, Frequency.ALWAYS, currentCalendar.getTime(), currentCalendar.getTime());
        System.out.println(entry == null? null: String.format("key:%d value:%.2f", entry.getKey(), entry.getValue()));
        entry = getRangePosition(10.71f,prices, Frequency.ALWAYS, currentCalendar.getTime(), currentCalendar.getTime());
        System.out.println(entry == null? null: String.format("key:%d value:%.2f", entry.getKey(), entry.getValue()));
        entry = getRangePosition(10.81f,prices, Frequency.ALWAYS, currentCalendar.getTime(), currentCalendar.getTime());
        System.out.println(entry == null? null: String.format("key:%d value:%.2f", entry.getKey(), entry.getValue()));
        entry = getRangePosition(7.5f,prices, Frequency.ALWAYS, currentCalendar.getTime(), currentCalendar.getTime());
        System.out.println(entry == null? null: String.format("key:%d value:%.2f", entry.getKey(), entry.getValue()));
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
