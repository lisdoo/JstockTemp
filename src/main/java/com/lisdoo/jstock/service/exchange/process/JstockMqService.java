package com.lisdoo.jstock.service.exchange.process;

import com.lisdoo.jstock.factory.JdbcFactory;
import com.lisdoo.jstock.factory.MqConsumeFactory;
import com.lisdoo.jstock.service.exchange.JstockProcessService;
import com.lisdoo.jstock.service.exchange.JstockRangeRepository;
import com.lisdoo.jstock.service.exchange.JstockRangeService;
import com.lisdoo.jstock.service.exchange.JstockRepository;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.sql.SQLException;

@Service
public class JstockMqService {

    private static final Log log = LogFactory.getLog(JstockMqService.class);

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

    public void start() throws SQLException, ClassNotFoundException {

        JdbcFactory.get();
        JdbcFactory.fillList();

        for (String code: JdbcFactory.stockList.split(",")) {
            MqConsumeFactory.get(code);
        }
        MqConsumeFactory.startAll();
    }

    public void stop() throws SQLException {

        JdbcFactory.release();
        MqConsumeFactory.stopAll();
        MqConsumeFactory.release();
    }
}
