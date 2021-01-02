package com.lisdoo.jstock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lisdoo.jstock.readtomq.Data;

import java.io.IOException;
import java.util.Iterator;
import java.util.StringTokenizer;

public class JstockConsumeHandler {

    public static String[] stockColumns = new String[34];
    static {
        StringTokenizer tokenizer = new StringTokenizer("DateTime,LastClose,Open,High,Low,Price,Volume,Amount,LastSettle,SettlePrice,OpenPosition,ClosePosition,BuyPrice,BuyVolume,SellPrice,SellVolume,PriceWeight,EntrustRatio,UpDown,EntrustDiff,UpDownRate,OutVolume,InVolume,AvePrice,VolumeRatio,PE,ExchangeRatio,LastVolume,VibrationRatio,DateTime,OpenTime,CloseTime,Name,Code", ",");
        for(int var13 = 0; tokenizer.hasMoreTokens(); stockColumns[var13++] = tokenizer.nextToken()) {
        }
    }

    public void handleMessage(String text) {
        System.out.println("Received: " + text);
    }

    public void handleMessage(JSONArray ja) throws Exception {

        System.out.println("Received: " + ja.toString());
        show(ja);
    }


    void show(JSONArray ja) throws Exception {

        Object innerJo = ja;
        int i = 0;
        Iterator var7 = ((JSONArray)innerJo).iterator();

        while(var7.hasNext()) {
            Object s = var7.next();
            System.out.println(String.format("%02d", i) + "\t" + String.format("%20s", stockColumns[i++]) + "\t" + s);
        }

        System.out.println("------------------------------");
    }
}
