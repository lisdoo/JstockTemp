package com.lisdoo.jstock.service.exchange;

import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lisdoo.jstock.readwrite.Read;
import com.lisdoo.jstock.service.exchange.exception.db.EntityExistException;
import com.lisdoo.jstock.service.exchange.exception.db.EntityNoneException;
import com.lisdoo.jstock.service.mqhandler.JstockConsumeHandler;
import com.lisdoo.jstock.factory.MqProductFactory;
import com.lisdoo.jstock.service.exchange.exception.NotInRangeException;
import com.lisdoo.jstock.service.exchange.exception.NotInTheTradingCycle;
import com.lisdoo.jstock.readwrite.Data;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback(false)
public class JstockProcessTests {

    private static final Log log = LogFactory.getLog(JstockProcessTests.class);

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

    @Autowired
    JstockProcessService jps;

    static String jstockCode = "000089";
    static ObjectMapper om = new ObjectMapper();

    @Before
    public void setUp() {

        MqProductFactory.get(jstockCode);
    }


    @Test
    @Transactional(propagation = Propagation.NEVER)
    public void test01() throws Exception, NotInTheTradingCycle, NotInRangeException, EntityNoneException, EntityExistException {

        Optional<Jstock> j = jr.findByCode(jstockCode);
        System.out.println(om.writeValueAsString(j.get()));

        for (JstockRange jr: j.get().getJstockRanges()) {
            JSONArray ja = (JSONArray) MqProductFactory.get(jstockCode).receiveAndConvert();
            for (int i=0; i<30000; i++) {
                if (jps.makeRange(jr, ja)) {
                    em.clear();
                    jr = jrr.findByJstock(j.get()).get();
                }
            }
        }
    }

    @Test
    public void test02() throws Exception {

        Optional<Jstock> j = jr.findByCode(jstockCode);
        System.out.println(om.writeValueAsString(j.get()));

        for (JstockRange jr: j.get().getJstockRanges()) {

            Predicate p = new Predicate<JSONArray>() {

                JstockRange jr;

                @Override
                public boolean equals(Object obj) {
                    jr = (JstockRange) obj;
                    return false;
                }

                @SneakyThrows
                @Override
                public boolean test(JSONArray ja) {

                    if (jps.makeRange(jr, ja)) {
//                        em.clear();
//                        jr = jrr.findById(jr.getId()).get();
                    }
                    return true;
                }
            };
            p.equals(jr);


            String folderInStr = "I:\\jstock\\";
            String[] foldersInStr = new File(folderInStr).list((f1, f2) -> {
                if (f2.contains("out")) return true;
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

    @After
    public void shutdown() {

        MqProductFactory.release();
    }
}
