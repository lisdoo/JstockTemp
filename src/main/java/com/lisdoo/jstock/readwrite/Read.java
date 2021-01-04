package com.lisdoo.jstock.readwrite;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.lisdoo.jstock.factory.MqProductFactory;

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

public class Read {

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
            testRead(fullFolder, p);
        }
    }

    public static void testRead(String filePath, Predicate p) throws Exception {

        int countRows = 0;
        int countRecords = 0;
        int countCorrect = 0;
        int countError = 0;
        Date startDateTime = new Date();

        File readFile = new File(filePath);
        float fileLen = readFile.length();
        float currentLen = 0;
        FileInputStream fis = new FileInputStream(readFile);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        String line = null;
        int counter = 0;

        try {
            while ((line = bufferedReader.readLine()) != null) {
//                System.out.println("this is {} line [" + counter++);


                if (!line.contains("callback") || (line.contains("callback") && line.indexOf("callback")>100)) continue;
                countRows++;
                currentLen += line.length();
                if (countRows > 9999 && countRows % 10000 == 0) System.out.println(String.format("%02.0f%% %d", currentLen/fileLen*100, countRows));

                String str = line.substring(line.indexOf("callback"));
                JSONObject jo = null;
                try {
                    jo = JSON.parseObject(str.substring(9, str.length() - 2));
                } catch (JSONException e) {
                    System.err.println("error line");
                    countError++;
                    continue;
                }
//                System.out.println(line);
                JSONArray ja = jo.getJSONArray("Data");
                ja = ja.getJSONArray(0);
                Iterator var4 = ja.iterator();

                while (var4.hasNext()) {
                    countRecords++;
                    Object innerJo = var4.next();
                    JSONArray innerJoArray = (JSONArray) innerJo;
                    if (innerJoArray.isEmpty()) {
                        countError++;
                        continue;
                    }
                    if (!(innerJoArray.get(0) instanceof Long)) {
                        countError++;
                        continue;
                    }
                    try {
//                        if (p.test(new Data(innerJoArray))) {
                        if (p.test(innerJoArray)) {
                            countCorrect++;
                        } else {
                            throw new Exception("Insert DB fail");
                        }
                    } catch (ParseException e) {
                        countError++;
                        System.err.println("error line");
                        continue;
                    } catch (ClassCastException e) {
                        countError++;
                        e.printStackTrace();
                        if (e.getMessage() == null) {
                            exit(line, 1, countRows, countRecords, countCorrect, countError, startDateTime);
                        } else if (e.getMessage().equalsIgnoreCase("java.math.BigInteger cannot be cast to java.lang.Integer")) {
                            continue;
                        } else if (e.getMessage().equalsIgnoreCase("java.lang.Long cannot be cast to java.lang.Integer")) {
                            continue;
                        } else {
                            exit(line, 1, countRows, countRecords, countCorrect, countError, startDateTime);
                        }
                    }
                }

//                if (counter == 55) break;
//                System.out.println("-------------------------------------------------");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            exit(line, 2, countRows, countRecords, countCorrect, countError, startDateTime);
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
            exit(line, 9, countRows, countRecords, countCorrect, countError, startDateTime);
        } catch (Exception e) {
            e.printStackTrace();
            exit(line, 3, countRows, countRecords, countCorrect, countError, startDateTime);
        }

        fis.close();
        isr.close();
        bufferedReader.close();

        System.out.println("countRows:      "+countRows);
        System.out.println("countRecords:   "+countRecords);
        System.out.println("countCorrect:   "+countCorrect);
        System.out.println("countError:     "+countError);
        System.out.println(startDateTime + "---->" + new Date());
    }

    static void exit(String line, int exitId, int countRows, int countRecords, int countCorrect, int countError, Date startDateTime) {

        System.out.println("countRows:      "+countRows);
        System.out.println("countRecords:   "+countRecords);
        System.out.println("countCorrect:   "+countCorrect);
        System.out.println("countError:     "+countError);
        System.out.println(startDateTime + "---->" + new Date());
//        System.exit(exitId);
    }
}

