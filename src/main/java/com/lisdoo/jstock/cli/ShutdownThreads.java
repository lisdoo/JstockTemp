package com.lisdoo.jstock.cli;

import com.lisdoo.jstock.SpringContextHolder;
import com.lisdoo.jstock.service.exchange.process.JstockMqService;
import lombok.SneakyThrows;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutdownThreads implements Job {

    Logger log = LoggerFactory.getLogger(ShutdownThreads.class);

    public ShutdownThreads() {
    }

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JstockMqService jms = (JstockMqService) SpringContextHolder.getBean("jstockMqService");
        jms.stop();
        log.info("stop mq consumer");
    }
}
