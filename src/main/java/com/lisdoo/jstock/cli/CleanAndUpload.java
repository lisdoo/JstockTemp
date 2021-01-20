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
import java.text.SimpleDateFormat;
import java.util.Date;

public class CleanAndUpload implements Job {

    Logger log = LoggerFactory.getLogger(CleanAndUpload.class);

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

    public CleanAndUpload() {
    }

    @SneakyThrows
    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {

        log.info("执行CleanAndUpload");

        Date currentDate = new Date();
        currentDate.setTime(currentDate.getTime()-3600*24*1000);
        String currentDateInStr = sdf.format(currentDate);
        String currentDateInStr2 = sdf2.format(currentDate);

        CmdRunner cmdRunner = (CmdRunner) SpringContextHolder.getBean("cmdRunner");
        StringBuffer sb = new StringBuffer();
        sb.append(String.format("-e cp?../stockslog/stocksinfo.%s?.", currentDateInStr));
        sb.append("\r\n");
        sb.append(String.format("-x %s-%s -y .* -d all -t all -v all -r all", currentDateInStr2, currentDateInStr2));
        sb.append("\r\n");
        sb.append("../stockslog/zipb.sh");
        sb.append("\r\n");
        cmdRunner.parse(false, 3, sb.toString());

        log.info("执行CleanAndUpload完成。");
    }

    public static void main(String[] args) {

        Date currentDate = new Date();
        currentDate.setTime(currentDate.getTime()-3600*24*1000);
        String currentDateInStr = sdf.format(currentDate);
        String currentDateInStr2 = sdf2.format(currentDate);

        System.out.println(String.format("-e cp?../stockslog/stocksinfo.%s?.", currentDateInStr));
        System.out.println(String.format("-x %s-%s -y .* -t all -v all -r all", currentDateInStr2, currentDateInStr2));
    }
}
