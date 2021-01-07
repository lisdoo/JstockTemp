package com.lisdoo.jstock.cli;

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

@Component
@Scope("prototype")
public class ShutdownThreads implements Job {

    Logger log = LoggerFactory.getLogger(ShutdownThreads.class);

    @Autowired
    JstockMqService jms;

    public ShutdownThreads() {
    }

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        jms.stop();
        log.info("stop mq consumer");
    }
}
