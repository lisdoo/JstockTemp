package com.lisdoo.jstock.service.exchange;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lisdoo.jstock.service.exchange.exception.db.EntityExistException;
import com.lisdoo.jstock.service.exchange.exception.db.EntityNoneException;
import com.lisdoo.jstock.service.mqhandler.JstockConsumeHandler;
import com.lisdoo.jstock.factory.MqProductFactory;
import com.lisdoo.jstock.service.exchange.exception.NotInRangeException;
import com.lisdoo.jstock.service.exchange.exception.NotInTheTradingCycle;
import com.lisdoo.jstock.readwrite.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class JstockProcessTests {

    private static final Log log = LogFactory.getLog(JstockProcessTests.class);

    @Autowired
    JstockRepository jr;

    @Autowired
    JstockRangeRepository jrr;

    @Autowired
    JstockRangeService jrs;

    static String jstockCode = "000089";
    static ObjectMapper om = new ObjectMapper();

    @Before
    public void setUp() {

        MqProductFactory.get(jstockCode);
    }

    public void makeRange(JstockRange jr) throws Exception, NotInTheTradingCycle, NotInRangeException, EntityExistException, EntityNoneException {

        Map<Integer, Float> range = Calculation.createRangeValue(jr.getBasePrise(), jr.getJstockStrategy().getPriceRange(), jr.getJstockStrategy().getCount(), jr.getJstockStrategy().getOffset());

        System.out.println(om.writeValueAsString(range));

        JSONArray ja = (JSONArray)MqProductFactory.get(jstockCode).receiveAndConvert();
        JstockConsumeHandler.show(ja);
        Data data = new Data(ja);
        System.out.println(data.getBuyPrice()/100);
        Map.Entry<Integer, Float> rangePosition = Calculation.getRangePosition(data.getBuyPrice()/100, range, Calculation.Frequency.valueOf(jr.getJstockStrategy().getFre()), jr.getChildrens().isEmpty()?null:null, data.getDateTime());
        System.out.println(om.writeValueAsString(rangePosition));

        if (rangePosition.getKey() == 0) {
            System.out.println("0档位无需处理");
            log.info(String.format("0档位无需处理"));
        } else {
            if (jr.getChildrens().isEmpty()) {

                System.out.println("Range不为0，但是其子集合为空，新建档位");
                log.info(String.format("Range不为0，但是其子集合为空，新建档位"));
                JstockRange jrTemp = new JstockRange(jr.getJstock(), rangePosition.getValue(), null, 0, 0f, 0f, null, jr, rangePosition.getKey(), null, new Date(), null);
                jrs.createJstockRange(jrTemp);
                if (rangePosition.getKey()<0) {
                    jrs.updateJstockRangeStatus(jr, Calculation.Status.BUY.name());
                } else {
                    jrs.updateJstockRangeStatus(jr, Calculation.Status.SELL.name());
                }

            } else {
                for (JstockRange jrChild: jr.getChildrens()) {
                    boolean isPresent = false;
                    if (rangePosition.getKey() == jrChild.getPosition()) {
                        isPresent = true;
                        System.out.println("重复档位");
                        log.info("重复档位");
                    }
                    if (isPresent) {
                        if (rangePosition.getKey()<0) {
                            jrs.updateJstockRangeStatus(jrChild, Calculation.Status.BUY.name());
                        } else {
                            jrs.updateJstockRangeStatus(jrChild, Calculation.Status.SELL.name());
                        }
                    } else {
                        System.out.println("Range不为0，但是其子集合中无此档位，新建档位");
                        log.info("Range不为0，但是其子集合中无此档位，新建档位");
                        JstockRange jrTemp = new JstockRange(jr.getJstock(), rangePosition.getValue(), null, 0, 0f, 0f, null, jr, rangePosition.getKey(), null, new Date(), null);
                        jrs.createJstockRange(jrTemp);
                        if (rangePosition.getKey()<0) {
                            jrs.updateJstockRangeStatus(jr, Calculation.Status.BUY.name());
                        } else {
                            jrs.updateJstockRangeStatus(jr, Calculation.Status.SELL.name());
                        }
                    }
                }
            }
        }
    }

    @Test
    public void test01() throws Exception, NotInTheTradingCycle, NotInRangeException, EntityNoneException, EntityExistException {

        Optional<Jstock> j = jr.findByCode(jstockCode);
        System.out.println(om.writeValueAsString(j.get()));

        for (JstockRange jr: j.get().getJstockRanges()) {
            makeRange(jr);
        }
    }

    @Test
    public void test02() throws Exception {

    }

    @After
    public void shutdown() {

        MqProductFactory.release();
    }
}
