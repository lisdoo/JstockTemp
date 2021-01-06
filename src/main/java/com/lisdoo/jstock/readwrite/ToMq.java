package com.lisdoo.jstock.readwrite;

import com.alibaba.fastjson.JSONArray;
import com.lisdoo.jstock.factory.MqProductFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.function.Predicate;

public class ToMq {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

    public static void main(String args[]) throws Exception {

        String jstockCode = "000089";
        MqProductFactory.get(jstockCode);

        Predicate p = new Predicate<JSONArray>() {

            @Override
            public boolean test(JSONArray o) {

                Data data = null;
                try {
                    data = new Data(o);
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }

                /*
                 * 跳过异常值
                 */
                if (!Filter.check(jstockCode, data)) {
                    return true;
                }

                if (jstockCode.equalsIgnoreCase(data.getCode())) {
                    MqProductFactory.get(jstockCode).convertAndSend(o);
                }
                return true;
            }
        };

        String folderInStr = "I:\\jstock\\";
        String[] foldersInStr = new File(folderInStr).list((f1, f2) -> {
//            if (f2.contains("_folder")) return true;
            if (f2.contains("out")) return true;
//            if (f2.contains("stocksinfo.2020-01-10_folder")) return true;
            return false;
        });

        Arrays.sort(foldersInStr);
        for (String str:foldersInStr) {
            System.out.println(str);
        }

        for (String folder: foldersInStr) {
            File f = new File(folderInStr, folder);
            for (String fullFolder : f.list()) {
                System.out.println(fullFolder);
                Read.testRead(new File(f, fullFolder).getAbsolutePath(), p);
            }
        }
    }
}

