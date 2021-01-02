package com.lisdoo.jstock.readtomq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lisdoo.jstock.MqProductFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.function.Predicate;

public class ToMq {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

    public static void main(String args[]) throws Exception {

        String jstockCode = "000089";
        MqProductFactory.get(jstockCode);

        Predicate p = new Predicate<JSONArray>() {

            @Override
            public boolean test(JSONArray o) {

                if (jstockCode.equalsIgnoreCase(o.getString(33))) {
                    MqProductFactory.get(jstockCode).convertAndSend(o);
                }
                return true;
            }
        };

        String folderInStr = "I:\\jstock\\";
        String[] foldersInStr = new File(folderInStr).list((f1, f2) -> {
            if (f2.contains("_folder")) return true;
//            if (f2.contains("stocksinfo.2020-01-10_folder")) return true;
            return false;
        });

        Arrays.sort(foldersInStr);
        for (String str:foldersInStr) {
            System.out.println(str);
        }

        for (String folder: foldersInStr) {
            String fullFolder = folderInStr + "\\" + folder + "\\" + "stocksinfo";
            System.out.println(fullFolder);
            Read.testRead(fullFolder, p);
        }
    }
}

