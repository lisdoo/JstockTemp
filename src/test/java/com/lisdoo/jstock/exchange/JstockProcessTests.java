package com.lisdoo.jstock.exchange;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lisdoo.jstock.Calculation;
import com.lisdoo.jstock.JstockConsumeHandler;
import com.lisdoo.jstock.MqProductFactory;
import com.lisdoo.jstock.exchange.exception.NotInRangeException;
import com.lisdoo.jstock.exchange.exception.NotInTheTradingCycle;
import com.lisdoo.jstock.readtomq.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.assertj.core.data.MapEntry;
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
    JstockRepository jstockRepository;

    @Autowired
    JstockRangeRepository jstockRangeRepository;

    static String jstockCode = "000089";
    static ObjectMapper om = new ObjectMapper();

    @Before
    public void setUp() {

        MqProductFactory.get(jstockCode);
    }

    public void makeRange(JstockRange jr) throws Exception, NotInTheTradingCycle, NotInRangeException {

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

                JstockRange jrChild = new JstockRange(jr.getJstock(), rangePosition.getValue(), jr.getJstockStrategy(), jr.getVolume(), jr.getAmount(), jr.getAveragePrise(), null, jr, rangePosition.getKey(), new Date(), null);

            } else {
                for (JstockRange jrChild: jr.getChildrens()) {
                    boolean isPresent = false;
                    if (rangePosition.getKey() == jrChild.getPosition()) {
                        isPresent = true;
                        System.out.println("重复档位");
                        log.info("重复档位");
                    }
                    if (!isPresent) {

                        System.out.println("Range不为0，但是其子集合中无此档位，新建档位");
                        log.info("Range不为0，但是其子集合中无此档位，新建档位");
                    }
                }
            }
        }
    }

    @Test
    public void test01() throws Exception, NotInTheTradingCycle, NotInRangeException {

        Optional<Jstock> j = jstockRepository.findByCode(jstockCode);
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
