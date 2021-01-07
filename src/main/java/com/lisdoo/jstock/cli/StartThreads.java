package com.lisdoo.jstock.cli;

import com.lisdoo.jstock.SpringContextHolder;
import com.lisdoo.jstock.service.exchange.process.JstockMqService;
import lombok.SneakyThrows;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

public class StartThreads implements Job {

    Logger log = LoggerFactory.getLogger(StartThreads.class);

    public StartThreads() {
    }

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        JstockMqService jms = (JstockMqService) SpringContextHolder.getBean("jstockMqService");
        jms.start();
        log.info("start mq consumer");
    }
}
