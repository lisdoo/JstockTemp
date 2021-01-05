package com.lisdoo.jstock.readwrite;

import com.alibaba.fastjson.JSONArray;
import com.lisdoo.jstock.service.exchange.Calculation;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.io.File;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class DbToInfluxdb {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

        final String serverURL = "http://influxdb:8086", username = "root", password = "root";
        final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);

        String databaseName = "hexun";
        influxDB.query(new Query("CREATE DATABASE " + databaseName));
        influxDB.setDatabase(databaseName);

        influxDB.enableBatch(BatchOptions.DEFAULTS);

        String jstockName = "深圳";
        String jstockCode = "000089";



        //1、导入驱动jar包
        //2、注册驱动
        Class.forName("com.mysql.jdbc.Driver");

        //3、获取数据库的连接对象
        Connection con = DriverManager.getConnection("jdbc:mysql://ali47:3306/jstock?allowMultiQueries=true&serverTimezone=GMT%2B8", "root", "lenovo.112");

        //4、定义sql语句
        String sql = "select quote_time, status, price from jstock_range_record;";

        //5、获取执行sql语句的对象
        Statement stat = con.createStatement();

        //6、执行sql并接收返回结果
        ResultSet rs = stat.executeQuery(sql);

        //7、处理结果
        while (rs.next()) {
            System.out.println(String.format("quote_time: %s \t status: %s \t price: %f", rs.getTimestamp("quote_time"), rs.getString("status"), rs.getFloat("price")));

            switch(Calculation.Status.valueOf(rs.getString("status"))) {
                case BUY: {
                    influxDB.write(Point.measurement("getBuyPrice")
                            .time(rs.getTimestamp("quote_time").getTime(), TimeUnit.MILLISECONDS)
                            .tag("name", jstockName)
                            .tag("code", jstockCode+"MY")
                            .addField("value", ((Float)(rs.getFloat("price")*100)).longValue())
                            .build());
                } break;
                case SELL: {
                    influxDB.write(Point.measurement("getSellPrice")
                            .time(rs.getTimestamp("quote_time").getTime(), TimeUnit.MILLISECONDS)
                            .tag("name", jstockName)
                            .tag("code", jstockCode+"MY")
                            .addField("value", ((Float)(rs.getFloat("price")*100)).longValue())
                            .build());
                } break;
            }
        }


        //8、释放资源
        stat.close();
        con.close();

        influxDB.close();
    }
}
