package com.lisdoo.jstock.service.mqhandler;

import com.alibaba.fastjson.JSONArray;
import com.lisdoo.jstock.service.exchange.Jstock;
import com.lisdoo.jstock.service.exchange.JstockProcessService;
import com.lisdoo.jstock.service.exchange.JstockRange;
import com.lisdoo.jstock.service.exchange.JstockRepository;
import com.lisdoo.jstock.service.exchange.exception.NotInRangeException;
import com.lisdoo.jstock.service.exchange.exception.NotInTheTradingCycle;
import com.lisdoo.jstock.service.exchange.exception.db.EntityExistException;
import com.lisdoo.jstock.service.exchange.exception.db.EntityNoneException;
import com.lisdoo.jstock.service.exchange.process.JstockMqService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.Optional;
import java.util.StringTokenizer;

@Component
@Scope("prototype")
@Transactional
public class JstockConsumeHandler {

    private static final Log log = LogFactory.getLog(JstockConsumeHandler.class);

    @Autowired
    JstockProcessService jps;

    @Autowired
    JstockRepository jr;

    String jstockCode;

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

//        System.out.println("Received: " + ja.toString());
//        show(ja);
        Optional<Jstock> j = jr.findByCode(jstockCode);
        for (JstockRange jr: j.get().getJstockRanges()) {
            try {
                jps.makeRange(jr, ja);
            } catch (NotInTheTradingCycle notInTheTradingCycle) {
                notInTheTradingCycle.printStackTrace();
                log.info("notInTheTradingCycle");
            } catch (NotInRangeException e) {
                // TODO
                e.printStackTrace();
            } catch (EntityExistException e) {
                log.error("EntityExistException");
                e.printStackTrace();
            } catch (EntityNoneException e) {
                log.error("EntityExistException");
                e.printStackTrace();
            }
        }
    }


    public void show(JSONArray ja) throws Exception {

        Object innerJo = ja;
        int i = 0;
        Iterator var7 = ((JSONArray)innerJo).iterator();

        while(var7.hasNext()) {
            Object s = var7.next();
            System.out.println(String.format("%02d", i) + "\t" + String.format("%20s", stockColumns[i++]) + "\t" + s);
        }

        System.out.println("------------------------------"+this.toString());
        Thread.sleep(3000);
    }

    public String getJstockCode() {
        return jstockCode;
    }

    public void setJstockCode(String jstockCode) {
        this.jstockCode = jstockCode;
    }
}
