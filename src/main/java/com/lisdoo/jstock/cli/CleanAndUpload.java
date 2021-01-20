package com.lisdoo.jstock.cli;

import com.lisdoo.jstock.SpringContextHolder;
import com.lisdoo.jstock.service.mqhandler.JstockConsumeHandler;
import lombok.SneakyThrows;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;

public class CleanAndUpload implements Job {

    Logger log = LoggerFactory.getLogger(CleanAndUpload.class);

    public CleanAndUpload() {
    }

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("执行CleanAndUpload");

        CmdRunner cmdRunner = (CmdRunner) SpringContextHolder.getBean("cmdRunner");
        cmdRunner.parse(false, "-e chdir");

        log.info("执行CleanAndUpload完成。");
    }
}
