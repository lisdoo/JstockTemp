package com.lisdoo.jstock.readtomq;

import com.alibaba.fastjson.JSONArray;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class ToInfluxdb {

    public static void main(String args[]) throws Exception {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

        final String serverURL = "http://influxdb:8086", username = "root", password = "root";
        final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);

        String databaseName = "hexun";
        influxDB.query(new Query("CREATE DATABASE " + databaseName));
        influxDB.setDatabase(databaseName);

        influxDB.enableBatch(BatchOptions.DEFAULTS);

        String jstockCode = "000089";

        Predicate p = new Predicate<JSONArray>() {

            @Override
            public boolean test(JSONArray o) {

                if (!jstockCode.equalsIgnoreCase(o.getString(33))) return true;

                Data data = null;
                try {
                    data = new Data(o);
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }

                influxDB.write(Point.measurement("getVolume")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getVolume().longValue())
                        .build());

                influxDB.write(Point.measurement("getAmount")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getAmount().longValue())
                        .build());

                influxDB.write(Point.measurement("getBuyPrice")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getBuyPrice().longValue())
                        .build());

                influxDB.write(Point.measurement("getBuyPrice1")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getBuyPrice1().longValue())
                        .build());

                influxDB.write(Point.measurement("getBuyPrice2")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getBuyPrice2().longValue())
                        .build());

                influxDB.write(Point.measurement("getBuyPrice3")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getBuyPrice3().longValue())
                        .build());

                influxDB.write(Point.measurement("getBuyPrice4")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getBuyPrice4().longValue())
                        .build());

                influxDB.write(Point.measurement("getBuyPrice5")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getBuyPrice5().longValue())
                        .build());

                influxDB.write(Point.measurement("getBuyVolume")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getBuyVolume().longValue())
                        .build());

                influxDB.write(Point.measurement("getBuyVolume1")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getBuyVolume1().longValue())
                        .build());

                influxDB.write(Point.measurement("getBuyVolume2")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getBuyVolume2().longValue())
                        .build());

                influxDB.write(Point.measurement("getBuyVolume3")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getBuyVolume3().longValue())
                        .build());

                influxDB.write(Point.measurement("getBuyVolume4")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getBuyVolume4().longValue())
                        .build());

                influxDB.write(Point.measurement("getBuyVolume5")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getBuyVolume5().longValue())
                        .build());

                influxDB.write(Point.measurement("getSellPrice")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getSellPrice().longValue())
                        .build());

                influxDB.write(Point.measurement("getSellPrice1")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getSellPrice1().longValue())
                        .build());

                influxDB.write(Point.measurement("getSellPrice2")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getSellPrice2().longValue())
                        .build());

                influxDB.write(Point.measurement("getSellPrice3")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getSellPrice3().longValue())
                        .build());

                influxDB.write(Point.measurement("getSellPrice4")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getSellPrice4().longValue())
                        .build());

                influxDB.write(Point.measurement("getSellPrice5")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getSellPrice5().longValue())
                        .build());

                influxDB.write(Point.measurement("getSellVolume")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getSellVolume().longValue())
                        .build());

                influxDB.write(Point.measurement("getSellVolume1")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getSellVolume1().longValue())
                        .build());

                influxDB.write(Point.measurement("getSellVolume2")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getSellVolume2().longValue())
                        .build());

                influxDB.write(Point.measurement("getSellVolume3")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getSellVolume3().longValue())
                        .build());

                influxDB.write(Point.measurement("getSellVolume4")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getSellVolume4().longValue())
                        .build());

                influxDB.write(Point.measurement("getSellVolume5")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getSellVolume5().longValue())
                        .build());

                influxDB.write(Point.measurement("getInVolume")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getInVolume().longValue())
                        .build());

                influxDB.write(Point.measurement("getOutVolume")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getOutVolume().longValue())
                        .build());

                influxDB.write(Point.measurement("getPE")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getPE().longValue())
                        .build());

                influxDB.write(Point.measurement("getLastVolume")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getLastVolume().longValue())
                        .build());

                return true;
            }
        };

//        Read.testRead("H:\\jstock\\stocksinfo.2020-05-26_folder\\stocksinfo", p);
//        Read.testRead("H:\\jstock\\stocksinfo.2020-05-25_folder\\stocksinfo", p);
//        Read.testRead("H:\\jstock\\stocksinfo.2020-05-20_folder\\stocksinfo", p);
//        Read.testRead("H:\\jstock\\stocksinfo.2020-05-19_folder\\stocksinfo", p);
//        Read.testRead("H:\\jstock\\stocksinfo.2020-05-18_folder\\stocksinfo", p);
//        Read.testRead("H:\\jstock\\stocksinfo.2020-05-15_folder\\stocksinfo", p);
//        Read.testRead("H:\\jstock\\stocksinfo.2020-06-01_folder\\stocksinfo", p);
        Read.testRead("I:\\jstock\\stocksinfo.2020-06-02_folder\\stocksinfo", p);

        influxDB.flush();
        influxDB.close();
    }
}
