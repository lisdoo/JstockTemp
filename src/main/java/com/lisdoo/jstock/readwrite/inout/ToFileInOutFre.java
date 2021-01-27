package com.lisdoo.jstock.readwrite.inout;

import com.alibaba.fastjson.JSONArray;
import com.lisdoo.jstock.readwrite.Data;
import com.lisdoo.jstock.readwrite.Read;
import com.lisdoo.jstock.service.exchange.exception.NotInTheTradingCycle;
import lombok.SneakyThrows;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.function.Predicate;

public class ToFileInOutFre {

    public static void main(String args[]) throws Exception, NotInTheTradingCycle {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String folderInStr = "I:\\jstock\\out\\codes";

        File out = new File(folderInStr, "all.csv");
        out.delete();
        FileOutputStream fos = new FileOutputStream(out);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        bos.write("Code,DateTime,Fre,\r\n".getBytes());



        Predicate p = new Predicate<JSONArray>() {

            Integer lastInVolume = 0;
            Integer lastOutVolume = 0;

            Integer changeInVolume = null;
            Integer changeOutVolume = null;

            @SneakyThrows
            @Override
            public boolean test(JSONArray innerJoArray) {

                Data data = null;
                try {
                    data = new Data(innerJoArray);
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }
                if (lastInVolume.equals(data.getInVolume())) {
                    changeInVolume = null;
                } else {
                    changeInVolume = data.getInVolume() - lastInVolume;
                    lastInVolume = data.getInVolume();
                }

                if (lastOutVolume.equals(data.getOutVolume())) {
                    changeOutVolume = null;
                } else {
                    changeOutVolume = data.getOutVolume() - lastOutVolume;
                    lastOutVolume = data.getOutVolume();
                }

                if (changeInVolume != null && changeInVolume < 0) {
                    return true;
                }
                if (changeOutVolume != null && changeOutVolume < 0) {
                    return true;
                }

                String line = String.format("\"%s\",%s,%d,%d,%d,%d,%.2f,%b,%s,%s,\r\n", data.getCode(), sdf2.format(data.getDateTime()), data.getInVolume(), data.getOutVolume(),data.getVolume(), data.getAmount(), Float.parseFloat(data.getPrice().toString())/100, data.getInVolume()+data.getOutVolume()==data.getVolume(),changeInVolume==null?"":changeInVolume.toString(),changeOutVolume==null?"":changeOutVolume.toString());
                System.out.print(line);
                bos.write(line.getBytes());

                return true;
            }
        };






        File[] folders = new File(folderInStr).listFiles((f1, f2) -> {
//            if (f2.contains("_folder")) return true;
            if (f2.contains("002180")) return true;
//            if (f2.contains("600519")) return true;
//            if (f2.contains("600487")) return true;
//            if (f2.contains("601600")) return true;
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
                Read.testRead(fullFolder.getAbsolutePath(), p, false);
//                break;
            }
        }

        bos.close();
    }
}
