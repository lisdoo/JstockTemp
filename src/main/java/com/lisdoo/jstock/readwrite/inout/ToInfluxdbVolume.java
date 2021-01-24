package com.lisdoo.jstock.readwrite.inout;

import com.lisdoo.jstock.readwrite.Data;
import com.lisdoo.jstock.readwrite.ReadCsv;
import com.lisdoo.jstock.service.exchange.exception.NotInTheTradingCycle;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;
import org.springframework.util.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class ToInfluxdbVolume {

    public static void main(String args[]) throws Exception, NotInTheTradingCycle {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        final String serverURL = "http://influxdb:8086", username = "root", password = "root";
        final InfluxDB influxDB = InfluxDBFactory.connect(serverURL, username, password);

        String databaseName = "hexunDailyVolume";
        influxDB.query(new Query("CREATE DATABASE " + databaseName));
        influxDB.setDatabase(databaseName);

        influxDB.enableBatch(BatchOptions.DEFAULTS);

        String[] lineCells = new String[11];

        Predicate p = new Predicate<String[]>() {

            @Override
            public boolean test(String[] o) {

                for (int i=0; i< lineCells.length; i++) {
                    lineCells[i] = null;
                }
                for (int i=0; i< o.length; i++) {
                    lineCells[i] = o[i];
                }

                Integer inVolume = lineCells[9]==null||lineCells[9].isEmpty()?null:Integer.decode(lineCells[9]);
                Integer outVolume = lineCells[10]==null||lineCells[10].isEmpty()?null:Integer.decode(lineCells[10]);
                Float currentPrise = Float.parseFloat(o[7]);
                Float inAmount = inVolume==null?null:inVolume*currentPrise;
                Float outAmount = outVolume==null?null:outVolume*currentPrise;

                Data data = null;
                try {
                    data = new Data();
                    data.setInVolume(inAmount==null?null:inAmount.intValue());
                    data.setOutVolume(outAmount==null?null:outAmount.intValue());

//                    data.setInVolume(lineCells[9]==null?null:((Float)(Integer.decode(lineCells[9])*Float.parseFloat(o[7]))).intValue());
//                    data.setOutVolume(lineCells[10]==null?null:((Float)(Integer.decode(lineCells[10])*Float.parseFloat(o[7]))).intValue());
                    data.setCode(o[1]);
                    data.setDateTime(sdf.parse(o[2]));
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
                System.out.println(String.format("%s %s=%d,%d", data.getDateTime().toString(), data.getCode(), data.getInVolume(), data.getOutVolume()));

                if (data.getInVolume()!=null) {
                    influxDB.write(Point.measurement("ChangeInVolume")
                            .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
//                        .tag("name", data.getName())
                            .tag("code", data.getCode())
                            .addField("value", data.getInVolume().longValue())
                            .build());
                }

                if (data.getOutVolume()!=null) {
                    influxDB.write(Point.measurement("ChangeOutVolume")
                            .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
//                        .tag("name", data.getName())
                            .tag("code", data.getCode())
                            .addField("value", data.getOutVolume().longValue())
                            .build());
                }

                if (currentPrise.intValue() != 0) {
                    influxDB.write(Point.measurement("Prise")
                            .time(data.getDateTime().getTime(), TimeUnit.MILLISECONDS)
//                        .tag("name", data.getName())
                            .tag("code", data.getCode())
                            .addField("value", currentPrise)
                            .build());
                }
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






        String folderInStr = "I:\\jstock\\out\\codes\\all.csv";

        ReadCsv.testRead(folderInStr, p);

        influxDB.close();
    }
}
