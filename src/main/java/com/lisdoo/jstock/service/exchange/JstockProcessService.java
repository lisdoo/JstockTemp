package com.lisdoo.jstock.service.exchange;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lisdoo.jstock.readwrite.Data;
import com.lisdoo.jstock.service.exchange.exception.NotInRangeException;
import com.lisdoo.jstock.service.exchange.exception.NotInTheTradingCycle;
import com.lisdoo.jstock.service.exchange.exception.db.EntityExistException;
import com.lisdoo.jstock.service.exchange.exception.db.EntityNoneException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class JstockProcessService {

    private static final Log log = LogFactory.getLog(JstockProcessService.class);

    @Autowired
    JstockRepository jr;

    @Autowired
    JstockRangeRepository jrr;

    @Autowired
    JstockRangeService jrs;

    @Autowired
    EntityManager em;

    @Autowired
    SessionFactory sf;

    ObjectMapper om = new ObjectMapper();

    public boolean tradingStrategy(Map.Entry<Integer, Float> rangePosition, JstockRange jr, Data data) throws EntityNoneException {

        Integer lastPosition = jr.getParent().getLastPosition() == null? 0: jr.getParent().getLastPosition();

        if (rangePosition.getKey() < 0) {
            if (lastPosition +1 < rangePosition.getKey()) {
                log.info(String.format("最后一次交易档位为：%d，加1后仍然小于当前档位：%d，执行SELL", lastPosition, rangePosition.getKey()));
                jrs.updateJstockRangeStatus(jr, rangePosition.getKey(), Calculation.Status.SELL.name(), data);
                return true;
            } else if (lastPosition -1> rangePosition.getKey()) {
                log.info(String.format("最后一次交易档位为：%d，减1后仍然大于当前档位：%d，执行BUY", lastPosition, rangePosition.getKey()));
                jrs.updateJstockRangeStatus(jr, rangePosition.getKey(), Calculation.Status.BUY.name(), data);
                return true;
            }
        } else {
            if (lastPosition > rangePosition.getKey()) {
                log.info(String.format("最后一次交易档位为：%d，小于当前档位：%d，执行SELL", lastPosition, rangePosition.getKey()));
                jrs.updateJstockRangeStatus(jr, rangePosition.getKey(), Calculation.Status.SELL.name(), data);
                return true;
            }
        }
        return false;
    }

    public boolean makeRange(JstockRange jr, JSONArray ja) throws Exception, NotInTheTradingCycle, NotInRangeException, EntityExistException, EntityNoneException {

        Map<Integer, Float> range = Calculation.createRangeValue(jr.getBasePrise(), jr.getJstockStrategy().getPriceRange(), jr.getJstockStrategy().getCount(), jr.getJstockStrategy().getOffset());

//        System.out.println(om.writeValueAsString(range));

//            JstockConsumeHandler.show(ja);
        Data data = new Data(ja);
//            System.out.println(data.getBuyPrice() / 100);
        Map.Entry<Integer, Float> rangePosition = Calculation.getRangePosition(data.getBuyPrice() / 100, range, Calculation.Frequency.valueOf(jr.getJstockStrategy().getFre()), jr.getLastTradeDate(), data.getDateTime());

        if (rangePosition.getKey() == 0) {
//            log.info(String.format("0档位无需处理"));
            return false;
        } else {
            if (jr.getChildrens().isEmpty()) {

                Map<String, Object> info = new HashMap<>();
                info.put("rangePosition", rangePosition);
                info.put("data", data);
                JstockRange jrTemp = new JstockRange(null, rangePosition.getValue(), jr.getJstockStrategy(), 0, 0f, 0f, null, jr, rangePosition.getKey(), rangePosition.getValue(), null, null, null, om.writeValueAsString(info), new Date(), null);
                jr.getChildrens().add(jrTemp);

                jrs.createJstockRange(jrTemp);
                return tradingStrategy(rangePosition, jrTemp, data);
            } else {
                boolean isPresent = false;
                JstockRange jrChild = null;
                for (JstockRange jrChildTemp : jr.getChildrens()) {
                    if (rangePosition.getKey().equals(jrChildTemp.getPosition())) {
                        isPresent = true;
                        jrChild = jrChildTemp;
                    }
                }
                if (isPresent) {
                    // 不同时候才触发
                    return tradingStrategy(rangePosition, jrChild, data);
                } else {
                    Map<String, Object> info = new HashMap<>();
                    info.put("rangePosition", rangePosition);
                    info.put("data", data);
                    JstockRange jrTemp = new JstockRange(null, rangePosition.getValue(), jr.getJstockStrategy(), 0, 0f, 0f, null, jr, rangePosition.getKey(), rangePosition.getValue(), null, null, null, om.writeValueAsString(info), new Date(), null);
                    jr.getChildrens().add(jrTemp);

                    jrs.createJstockRange(jrTemp);
                    return tradingStrategy(rangePosition, jrTemp, data);
                }
            }
        }
    }
}
