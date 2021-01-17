package com.lisdoo.jstock.readwrite.volume;

import com.alibaba.fastjson.JSONArray;
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

public class ToInfluxdbVolume {

    public static void main(String args[]) throws Exception, NotInTheTradingCycle {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

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
                    data.setAmount(Long.decode(o[3]));
                    data.setVolume(Long.decode(o[2]));
                    data.setCode(o[1]);
                    data.setDateTime(sdf.parse(o[0]));
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
                if(data.getAmount() == 0) return true;
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
//                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
//                        .tag("name", data.getName())
//                        .tag("code", data.getCode())
//                        .addField("value", data.getInVolume().longValue())
//                        .build());
//
//                influxDB.write(Point.measurement("getOutVolume")
//                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
//                        .tag("name", data.getName())
//                        .tag("code", data.getCode())
//                        .addField("value", data.getOutVolume().longValue())
//                        .build());
//
//                influxDB.write(Point.measurement("getPE")
//                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
//                        .tag("name", data.getName())
//                        .tag("code", data.getCode())
//                        .addField("value", data.getPE().longValue())
//                        .build());
//
//                influxDB.write(Point.measurement("getLastVolume")
//                        .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
//                        .tag("name", data.getName())
//                        .tag("code", data.getCode())
//                        .addField("value", data.getLastVolume().longValue())
//                        .build());

                return true;
            }
        };






        String folderInStr = "I:\\jstock\\out";
        File[] folders = new File(folderInStr).listFiles((f1, f2) -> {
//            if (f2.contains("_folder")) return true;
            if (f2.contains("process")) return true;
//            if (f2.contains("stocksinfo.2020-01-10_folder")) return true;
            return false;
        });

        Arrays.sort(folders);
        for (File str:folders) {
            System.out.println(str.getName());
        }

        for (File folder: folders) {
            for (File fullFolder : folder.listFiles()) {
                System.out.println(fullFolder.getName());
                ReadCsv.testRead(fullFolder.getAbsolutePath(), p);
            }
        }

        influxDB.close();
    }
}
