package com.lisdoo.jstock.readwrite.volume;

import com.lisdoo.jstock.readwrite.Data;
import com.lisdoo.jstock.readwrite.ReadCsv;
import com.lisdoo.jstock.service.exchange.exception.NotInTheTradingCycle;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class AllAppendToInfluxdbVolume {

    public static void main(String args[]) throws Exception, NotInTheTradingCycle {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");

        final String serverURL = "http://influxdb:8086", username = "root", password = "root";
        final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);

        String databaseName = "hexunVolume";
        influxDB.query(new Query("CREATE DATABASE " + databaseName));
        influxDB.setDatabase(databaseName);

        influxDB.enableBatch(BatchOptions.DEFAULTS);

        Predicate p = new Predicate<String[]>() {

            @Override
            public boolean test(String[] o) {

                Data data = null;
                try {
                    data = new Data();
                    data.setAmount(Long.decode(o[4]));
                    data.setVolume(Long.decode(o[3]));
                    data.setCode(o[1].replaceAll("\"", ""));
                    data.setDateTime(sdf2.parse(o[2].replaceAll("\"", "")));
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
                if (data.getAmount() == 0) return true;
                System.out.println(String.format("%s %s=%d,%d", data.getDateTime().toString(), data.getCode(), data.getVolume(), data.getAmount()));

                influxDB.write(Point.measurement("getVolume")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
//                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getVolume().longValue())
                        .build());

                influxDB.write(Point.measurement("getAmount")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
//                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", data.getAmount().longValue())
                        .build());

//                influxDB.write(Point.measurement("getInVolume")
//                        .time(data.getDateTime().getTime(), TimeUnit.DAYS)
//                        .tag("name", data.getName())
//                        .tag("code", data.getCode())
//                        .addField("value", data.getInVolume().longValue())
//                        .build());
//
//                influxDB.write(Point.measurement("getOutVolume")
//                        .time(data.getDateTime().getTime(), TimeUnit.DAYS)
//                        .tag("name", data.getName())
//                        .tag("code", data.getCode())
//                        .addField("value", data.getOutVolume().longValue())
//                        .build());
//
//                influxDB.write(Point.measurement("getPE")
//                        .time(data.getDateTime().getTime(), TimeUnit.DAYS)
//                        .tag("name", data.getName())
//                        .tag("code", data.getCode())
//                        .addField("value", data.getPE().longValue())
//                        .build());
//
                influxDB.write(Point.measurement("percentage")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
//                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", Double.parseDouble(o[7]))
                        .build());

                influxDB.write(Point.measurement("ranking")
                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
//                        .tag("name", data.getName())
                        .tag("code", data.getCode())
                        .addField("value", Integer.parseInt(o[8]))
                        .build());

                return true;
            }
        };


        String folderInStr = "I:\\jstock\\out";

        ReadCsv.testRead(folderInStr + "\\all-append.csv", p);

        influxDB.close();
    }
}
