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

import java.util.Date;
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

    Date startDate;

    Integer count;
    Integer countNotInTheTradingCycle;
    Integer countNotInRangeException;
    Integer countEntityExistException;
    Integer countEntityNoneException;
    Integer countNumberFormatException;
    Integer countException;

    public JstockConsumeHandler() {
        count = 0;
        countNotInTheTradingCycle = 0;
        countNotInRangeException = 0;
        countEntityExistException = 0;
        countEntityNoneException = 0;
        countNumberFormatException = 0;
        countException = 0;
        startDate = new Date();
    }

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
        count++;
//        System.out.println("Received: " + ja.toString());
//        show(ja);
        Optional<Jstock> j = jr.findByCode(jstockCode);
        for (JstockRange jr: j.get().getJstockRanges()) {
            if ((jr.getStatus()) == null || (!jr.getStatus().equalsIgnoreCase("on"))) continue;
            try {
                jps.makeRange(jr, ja);
            } catch (NotInTheTradingCycle e) {
                countNotInTheTradingCycle++;
//                log.error(String.format("JstockCode: %s error: %s exception: %s", jr.getJstock().getCode(), e.getMessage(), "NotInTheTradingCycle"));
            } catch (NotInRangeException e) {
                countNotInRangeException++;
                log.error(String.format("JstockCode: %s error: %s exception: %s", jr.getJstock().getCode(), e.getMessage(), "NotInRangeException"));
            } catch (EntityExistException e) {
                countEntityExistException++;
                log.error(String.format("JstockCode: %s error: %s exception: %s", jr.getJstock().getCode(), e.getMessage(), "EntityExistException"));
            } catch (EntityNoneException e) {
                countEntityNoneException++;
                log.error(String.format("JstockCode: %s error: %s exception: %s", jr.getJstock().getCode(), e.getMessage(), "EntityNoneException"));
            } catch (NumberFormatException e) {
                countNumberFormatException++;
                e.printStackTrace();
                log.error(String.format("JstockCode: %s error: %s exception: %s", jr.getJstock().getCode(), e.getMessage(), "NumberFormatException"));
            } catch (Exception e) {
                countException++;
                e.printStackTrace();
                log.error(String.format("JstockCode: %s error: %s exception: %s", jr.getJstock().getCode(), e.getMessage(), "Exception"));
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

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public Integer getCountNotInTheTradingCycle() {
        return countNotInTheTradingCycle;
    }

    public void setCountNotInTheTradingCycle(Integer countNotInTheTradingCycle) {
        this.countNotInTheTradingCycle = countNotInTheTradingCycle;
    }

    public Integer getCountNotInRangeException() {
        return countNotInRangeException;
    }

    public void setCountNotInRangeException(Integer countNotInRangeException) {
        this.countNotInRangeException = countNotInRangeException;
    }

    public Integer getCountEntityExistException() {
        return countEntityExistException;
    }

    public void setCountEntityExistException(Integer countEntityExistException) {
        this.countEntityExistException = countEntityExistException;
    }

    public Integer getCountEntityNoneException() {
        return countEntityNoneException;
    }

    public void setCountEntityNoneException(Integer countEntityNoneException) {
        this.countEntityNoneException = countEntityNoneException;
    }

    public Integer getCountNumberFormatException() {
        return countNumberFormatException;
    }

    public void setCountNumberFormatException(Integer countNumberFormatException) {
        this.countNumberFormatException = countNumberFormatException;
    }

    public Integer getCountException() {
        return countException;
    }

    public void setCountException(Integer countException) {
        this.countException = countException;
    }
}
