package com.lisdoo.jstock.readtomq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;

public class Data implements Serializable {

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");

    Date Date;             // 时间
    Integer	LastClose;     //
    Integer	Open;
    Integer	High;
    Integer	Low;
    Integer	Price;
    Long	Volume;
    Long	Amount;
    Integer	LastSettle;
    Integer	SettlePrice;
    Integer	OpenPosition;
    Integer	ClosePosition;
    Float	BuyPrice;
    Float	BuyPrice1;
    Float	BuyPrice2;
    Float	BuyPrice3;
    Float	BuyPrice4;
    Float	BuyPrice5;
    BigInteger BuyVolume;
    BigInteger	BuyVolume1;
    BigInteger	BuyVolume2;
    BigInteger	BuyVolume3;
    BigInteger	BuyVolume4;
    BigInteger	BuyVolume5;
    Float	SellPrice;
    Float	SellPrice1;
    Float	SellPrice2;
    Float	SellPrice3;
    Float	SellPrice4;
    Float	SellPrice5;
    BigInteger	SellVolume;
    BigInteger	SellVolume1;
    BigInteger	SellVolume2;
    BigInteger	SellVolume3;
    BigInteger	SellVolume4;
    BigInteger	SellVolume5;
    Integer	PriceWeight;
    Integer	EntrustRatio;
    Integer	UpDown;
    Integer	EntrustDiff;
    Integer	UpDownRate;
    Integer	OutVolume;
    Integer	InVolume;
    Integer	AvePrice;
    Integer	VolumeRatio;
    Integer	PE;
    Integer	ExchangeRatio;
    Integer	LastVolume;
    Integer	VibrationRatio;
    Date	DateTime;
    Date	OpenTime;
    Date	CloseTime;
    String	Name;
    String	Code;

    public Data() {
    }

    public Data(JSONArray innerJoArray) throws Exception {

        try {
            Object tempO = null;

            Iterator var7 = (innerJoArray).iterator();

            // DateTime
            Long _1 = (Long) var7.next();
            setDate(sdf.parse(Long.toString(_1)));
            // LastClose
            Integer _2 = (Integer) var7.next();
            setLastClose(_2);
            // Open
            Integer _3 = (Integer) var7.next();
            setOpen(_3);
            // High
            Integer _4 = (Integer) var7.next();
            setHigh(_4);
            // Low
            Integer _5 = (Integer) var7.next();
            setLow(_5);
            // Price
            Integer _6 = (Integer) var7.next();
            setPrice(_6);
            // Volume
            Object _7o = var7.next();
            Long _7 = -1L;
            if (_7o instanceof Integer) {
                _7 = new Long((Integer) _7o);
            } else if (_7o instanceof Long) {
                _7 = (Long) _7o;
            } else {
                throw new Exception("parse Volume error.");
            }
            setVolume(_7);
            // Amount
            Object _8o = var7.next();
            Long _8 = -1L;
            if (_8o instanceof Integer) {
                _8 = new Long((Integer) _8o);
            } else if (_8o instanceof Long) {
                _8 = (Long) _8o;
            } else {
                throw new Exception("parse Amount error.");
            }
            setAmount(_8);
            // LastSettle
            Integer _9 = (Integer) var7.next();
            setLastSettle(_9);
            // SettlePrice
            Integer _10 = (Integer) var7.next();
            setSettlePrice(_10);
            // OpenPosition
            Integer _11 = (Integer) var7.next();
            setOpenPosition(_11);
            // ClosePosition
            Integer _12 = (Integer) var7.next();
            setClosePosition(_12);
            // BuyPrice
            Iterator _13Iterator = ((JSONArray) var7.next()).iterator();
            setBuyPrice((new Float((Integer) _13Iterator.next())));
            setBuyPrice1(getBuyPrice());
            setBuyPrice2((new Float((Integer) _13Iterator.next())));
            setBuyPrice3((new Float((Integer) _13Iterator.next())));
            setBuyPrice4((new Float((Integer) _13Iterator.next())));
            setBuyPrice5((new Float((Integer) _13Iterator.next())));
            // BuyVolume
            Iterator _14Iterator = ((JSONArray) var7.next()).iterator();
            setBuyVolume((tempO = _14Iterator.next()) instanceof BigInteger? (BigInteger)tempO: tempO instanceof Long? new BigInteger(((Long)tempO).toString()): new BigInteger(((Integer)tempO).toString()));
            setBuyVolume1(getBuyVolume());
            setBuyVolume2((tempO = _14Iterator.next()) instanceof BigInteger? (BigInteger)tempO: tempO instanceof Long? new BigInteger(((Long)tempO).toString()): new BigInteger(((Integer)tempO).toString()));
            setBuyVolume3((tempO = _14Iterator.next()) instanceof BigInteger? (BigInteger)tempO: tempO instanceof Long? new BigInteger(((Long)tempO).toString()): new BigInteger(((Integer)tempO).toString()));
            setBuyVolume4((tempO = _14Iterator.next()) instanceof BigInteger? (BigInteger)tempO: tempO instanceof Long? new BigInteger(((Long)tempO).toString()): new BigInteger(((Integer)tempO).toString()));
            setBuyVolume5((tempO = _14Iterator.next()) instanceof BigInteger? (BigInteger)tempO: tempO instanceof Long? new BigInteger(((Long)tempO).toString()): new BigInteger(((Integer)tempO).toString()));
            // SellPrice
            Iterator _15Iterator = ((JSONArray) var7.next()).iterator();
            setSellPrice((new Float((Integer) _15Iterator.next())));
            setSellPrice1(getSellPrice());
            setSellPrice2((new Float((Integer) _15Iterator.next())));
            setSellPrice3((new Float((Integer) _15Iterator.next())));
            setSellPrice4((new Float((Integer) _15Iterator.next())));
            setSellPrice5((new Float((Integer) _15Iterator.next())));
            // SellVolume
            Iterator _16Iterator = ((JSONArray) var7.next()).iterator();
            setSellVolume((tempO = _16Iterator.next()) instanceof BigInteger? (BigInteger)tempO: tempO instanceof Long? new BigInteger(((Long)tempO).toString()): new BigInteger(((Integer)tempO).toString()));
            setSellVolume1(getSellVolume());
            setSellVolume2((tempO = _16Iterator.next()) instanceof BigInteger? (BigInteger)tempO: tempO instanceof Long? new BigInteger(((Long)tempO).toString()): new BigInteger(((Integer)tempO).toString()));
            setSellVolume3((tempO = _16Iterator.next()) instanceof BigInteger? (BigInteger)tempO: tempO instanceof Long? new BigInteger(((Long)tempO).toString()): new BigInteger(((Integer)tempO).toString()));
            setSellVolume4((tempO = _16Iterator.next()) instanceof BigInteger? (BigInteger)tempO: tempO instanceof Long? new BigInteger(((Long)tempO).toString()): new BigInteger(((Integer)tempO).toString()));
            setSellVolume5((tempO = _16Iterator.next()) instanceof BigInteger? (BigInteger)tempO: tempO instanceof Long? new BigInteger(((Long)tempO).toString()): new BigInteger(((Integer)tempO).toString()));
            // PriceWeight
            Integer _17 = (Integer) var7.next();
            setPriceWeight(_17);
            // EntrustRatio
            Integer _18 = (Integer) var7.next();
            setEntrustRatio(_18);
            // UpDown
            Integer _19 = (Integer) var7.next();
            setUpDown(_19);
            // EntrustDiff
            Integer _20 = (Integer) var7.next();
            setEntrustDiff(_20);
            // UpDownRate
            Integer _21 = (Integer) var7.next();
            setUpDownRate(_21);
            // OutVolume
            Integer _22 = (Integer) var7.next();
            setOutVolume(_22);
            // InVolume
            Integer _23 = (Integer) var7.next();
            setInVolume(_23);
            // AvePrice
            Integer _24 = (Integer) var7.next();
            setAvePrice(_24);
            // VolumeRatio
            Integer _25 = (Integer) var7.next();
            setVolumeRatio(_25);
            // PE
            Integer _26 = (Integer) var7.next();
            setPE(_26);
            // ExchangeRatio
            Integer _27 = (Integer) var7.next();
            setExchangeRatio(_27);
            // LastVolume
            Integer _28 = (Integer) var7.next();
            setLastVolume(_28);
            // VibrationRatio
            Integer _29 = (Integer) var7.next();
            setVibrationRatio(_29);
            // DateTime
            Long _30 = (Long) var7.next();
            setDateTime(sdf.parse(Long.toString(_1)));
            // OpenTime
            Long _31 = (Long) var7.next();
            setOpenTime(sdf.parse(Long.toString(_31)));
            // CloseTime
            Long _32 = (Long) var7.next();
            setCloseTime(sdf.parse(Long.toString(_32)));
            // Name
            String _33 = (String) var7.next();
            setName(_33);
            // Code
            String _34 = (String) var7.next();
            setCode(_34);
        } catch (ClassCastException e) {
            System.err.println(innerJoArray.toJSONString());
            throw e;
        }
    }

    public String toString() {
        return JSON.toJSONString(this);
    }

    public java.util.Date getDate() {
        return Date;
    }

    public void setDate(java.util.Date date) {
        Date = date;
    }



    public Date getDateTime() {
        return DateTime;
    }

    public void setDateTime(Date dateTime) {
        DateTime = dateTime;
    }

    public Integer getLastClose() {
        return LastClose;
    }

    public void setLastClose(Integer lastClose) {
        LastClose = lastClose;
    }

    public Integer getOpen() {
        return Open;
    }

    public void setOpen(Integer open) {
        Open = open;
    }

    public Integer getHigh() {
        return High;
    }

    public void setHigh(Integer high) {
        High = high;
    }

    public Integer getLow() {
        return Low;
    }

    public void setLow(Integer low) {
        Low = low;
    }

    public Integer getPrice() {
        return Price;
    }

    public void setPrice(Integer price) {
        Price = price;
    }

    public Long getVolume() {
        return Volume;
    }

    public void setVolume(Long volume) {
        Volume = volume;
    }

    public Long getAmount() {
        return Amount;
    }

    public void setAmount(Long amount) {
        Amount = amount;
    }

    public BigInteger getBuyVolume() {
        return BuyVolume;
    }

    public void setBuyVolume(BigInteger buyVolume) {
        BuyVolume = buyVolume;
    }

    public BigInteger getBuyVolume1() {
        return BuyVolume1;
    }

    public void setBuyVolume1(BigInteger buyVolume1) {
        BuyVolume1 = buyVolume1;
    }

    public BigInteger getBuyVolume2() {
        return BuyVolume2;
    }

    public void setBuyVolume2(BigInteger buyVolume2) {
        BuyVolume2 = buyVolume2;
    }

    public BigInteger getBuyVolume3() {
        return BuyVolume3;
    }

    public void setBuyVolume3(BigInteger buyVolume3) {
        BuyVolume3 = buyVolume3;
    }

    public BigInteger getBuyVolume4() {
        return BuyVolume4;
    }

    public void setBuyVolume4(BigInteger buyVolume4) {
        BuyVolume4 = buyVolume4;
    }

    public BigInteger getBuyVolume5() {
        return BuyVolume5;
    }

    public void setBuyVolume5(BigInteger buyVolume5) {
        BuyVolume5 = buyVolume5;
    }

    public BigInteger getSellVolume() {
        return SellVolume;
    }

    public void setSellVolume(BigInteger sellVolume) {
        SellVolume = sellVolume;
    }

    public BigInteger getSellVolume1() {
        return SellVolume1;
    }

    public void setSellVolume1(BigInteger sellVolume1) {
        SellVolume1 = sellVolume1;
    }

    public BigInteger getSellVolume2() {
        return SellVolume2;
    }

    public void setSellVolume2(BigInteger sellVolume2) {
        SellVolume2 = sellVolume2;
    }

    public BigInteger getSellVolume3() {
        return SellVolume3;
    }

    public void setSellVolume3(BigInteger sellVolume3) {
        SellVolume3 = sellVolume3;
    }

    public BigInteger getSellVolume4() {
        return SellVolume4;
    }

    public void setSellVolume4(BigInteger sellVolume4) {
        SellVolume4 = sellVolume4;
    }

    public BigInteger getSellVolume5() {
        return SellVolume5;
    }

    public void setSellVolume5(BigInteger sellVolume5) {
        SellVolume5 = sellVolume5;
    }

    public Float getBuyPrice() {
        return BuyPrice;
    }

    public void setBuyPrice(Float buyPrice) {
        BuyPrice = buyPrice;
    }

    public Float getBuyPrice1() {
        return BuyPrice1;
    }

    public void setBuyPrice1(Float buyPrice1) {
        BuyPrice1 = buyPrice1;
    }

    public Float getBuyPrice2() {
        return BuyPrice2;
    }

    public void setBuyPrice2(Float buyPrice2) {
        BuyPrice2 = buyPrice2;
    }

    public Float getBuyPrice3() {
        return BuyPrice3;
    }

    public void setBuyPrice3(Float buyPrice3) {
        BuyPrice3 = buyPrice3;
    }

    public Float getBuyPrice4() {
        return BuyPrice4;
    }

    public void setBuyPrice4(Float buyPrice4) {
        BuyPrice4 = buyPrice4;
    }

    public Float getBuyPrice5() {
        return BuyPrice5;
    }

    public void setBuyPrice5(Float buyPrice5) {
        BuyPrice5 = buyPrice5;
    }

    public Float getSellPrice() {
        return SellPrice;
    }

    public void setSellPrice(Float sellPrice) {
        SellPrice = sellPrice;
    }

    public Float getSellPrice1() {
        return SellPrice1;
    }

    public void setSellPrice1(Float sellPrice1) {
        SellPrice1 = sellPrice1;
    }

    public Float getSellPrice2() {
        return SellPrice2;
    }

    public void setSellPrice2(Float sellPrice2) {
        SellPrice2 = sellPrice2;
    }

    public Float getSellPrice3() {
        return SellPrice3;
    }

    public void setSellPrice3(Float sellPrice3) {
        SellPrice3 = sellPrice3;
    }

    public Float getSellPrice4() {
        return SellPrice4;
    }

    public void setSellPrice4(Float sellPrice4) {
        SellPrice4 = sellPrice4;
    }

    public Float getSellPrice5() {
        return SellPrice5;
    }

    public void setSellPrice5(Float sellPrice5) {
        SellPrice5 = sellPrice5;
    }

    public Integer getLastSettle() {
        return LastSettle;
    }

    public void setLastSettle(Integer lastSettle) {
        LastSettle = lastSettle;
    }

    public Integer getSettlePrice() {
        return SettlePrice;
    }

    public void setSettlePrice(Integer settlePrice) {
        SettlePrice = settlePrice;
    }

    public Integer getOpenPosition() {
        return OpenPosition;
    }

    public void setOpenPosition(Integer openPosition) {
        OpenPosition = openPosition;
    }

    public Integer getClosePosition() {
        return ClosePosition;
    }

    public void setClosePosition(Integer closePosition) {
        ClosePosition = closePosition;
    }


    public Integer getPriceWeight() {
        return PriceWeight;
    }

    public void setPriceWeight(Integer priceWeight) {
        PriceWeight = priceWeight;
    }

    public Integer getEntrustRatio() {
        return EntrustRatio;
    }

    public void setEntrustRatio(Integer entrustRatio) {
        EntrustRatio = entrustRatio;
    }

    public Integer getUpDown() {
        return UpDown;
    }

    public void setUpDown(Integer upDown) {
        UpDown = upDown;
    }

    public Integer getEntrustDiff() {
        return EntrustDiff;
    }

    public void setEntrustDiff(Integer entrustDiff) {
        EntrustDiff = entrustDiff;
    }

    public Integer getUpDownRate() {
        return UpDownRate;
    }

    public void setUpDownRate(Integer upDownRate) {
        UpDownRate = upDownRate;
    }

    public Integer getOutVolume() {
        return OutVolume;
    }

    public void setOutVolume(Integer outVolume) {
        OutVolume = outVolume;
    }

    public Integer getInVolume() {
        return InVolume;
    }

    public void setInVolume(Integer inVolume) {
        InVolume = inVolume;
    }

    public Integer getAvePrice() {
        return AvePrice;
    }

    public void setAvePrice(Integer avePrice) {
        AvePrice = avePrice;
    }

    public Integer getVolumeRatio() {
        return VolumeRatio;
    }

    public void setVolumeRatio(Integer volumeRatio) {
        VolumeRatio = volumeRatio;
    }

    public Integer getPE() {
        return PE;
    }

    public void setPE(Integer PE) {
        this.PE = PE;
    }

    public Integer getExchangeRatio() {
        return ExchangeRatio;
    }

    public void setExchangeRatio(Integer exchangeRatio) {
        ExchangeRatio = exchangeRatio;
    }

    public Integer getLastVolume() {
        return LastVolume;
    }

    public void setLastVolume(Integer lastVolume) {
        LastVolume = lastVolume;
    }

    public Integer getVibrationRatio() {
        return VibrationRatio;
    }

    public void setVibrationRatio(Integer vibrationRatio) {
        VibrationRatio = vibrationRatio;
    }

    public Date getOpenTime() {
        return OpenTime;
    }

    public void setOpenTime(Date openTime) {
        OpenTime = openTime;
    }

    public Date getCloseTime() {
        return CloseTime;
    }

    public void setCloseTime(Date closeTime) {
        CloseTime = closeTime;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }
}
