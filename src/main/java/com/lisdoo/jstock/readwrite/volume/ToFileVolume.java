package com.lisdoo.jstock.readwrite.volume;

import com.lisdoo.jstock.readwrite.Data;
import com.lisdoo.jstock.readwrite.ReadCsv;
import com.lisdoo.jstock.service.exchange.exception.NotInTheTradingCycle;
import lombok.SneakyThrows;
import org.influxdb.BatchOptions;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.Point;
import org.influxdb.dto.Query;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

public class ToFileVolume {

    public static void main(String args[]) throws Exception, NotInTheTradingCycle {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String folderInStr = "I:\\jstock\\out";

        File out = new File(folderInStr, "all.csv");
        out.delete();
        FileOutputStream fos = new FileOutputStream(out);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write("Code,Date,Volume,Amount\r\n".getBytes());



        Predicate p = new Predicate<String[]>() {

            @SneakyThrows
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
                String line = String.format("\"%s\",%s,%d,%d\r\n", data.getCode(), sdf2.format(data.getDateTime()), data.getVolume(), data.getAmount());
                System.out.print(line);
                bos.write(line.getBytes());

                return true;
            }
        };






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

        bos.close();
    }
}
