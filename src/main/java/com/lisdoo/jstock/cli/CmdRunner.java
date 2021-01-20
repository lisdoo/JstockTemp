package com.lisdoo.jstock.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lisdoo.jstock.service.exchange.process.JstockMqService;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.*;

@Component
public class CmdRunner implements CommandLineRunner {

    private static final Log log = LogFactory.getLog(CmdRunner.class);

    private static SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

    List<Command> list = new ArrayList<>();


    @Autowired
    JstockMqService jms;

    @Override
    public void run(String... args) throws Exception {

        if (args.length != 0 && args[0].equalsIgnoreCase("silence")) {
            this.log.info(String.format("%s 模式，不启动MQ定时器。", args[0]));
        } else {

            this.log.info("------- Initializing -------------------");
            SchedulerFactory sf = new StdSchedulerFactory();
            Scheduler sched = sf.getScheduler();
            this.log.info("------- Initialization Complete --------");

            this.schedulingJobs(sched, StartThreads.class, "0 20 9 ? * MON-FRI", ShutdownThreads.class, "0 1 15 ? * MON-FRI");
            this.schedulingJob(sched, CleanAndUpload.class, "0/5 * * ? * MON-FRI");

            this.log.info("------- Starting Scheduler ----------------");
            sched.start();
            this.log.info("------- Started Scheduler -----------------");
        }

        Runnable r = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {

                parse(true, null);

            }
        };
        new Thread(r).start();
    }

    public void parse(boolean run, String cmd) throws IOException {

        ObjectMapper om = new ObjectMapper();

        // create the command line parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption( "a", "start", false, "Start." );
        options.addOption( "b", "stop", false, "Stop." );
        options.addOption( "x", "time", true, "get time list. parameters: yyyyMMdd-yyyyMMdd" );
        options.addOption( "y", "codes", true, "get codes." );
        options.addOption( "d", "download", true, "download." );
        options.addOption( "t", "to file", true, "to file. parameters: jstockCode-yyyyMMdd" );
        options.addOption( "p", "process", true, "to file. parameters: jstockCode-yyyyMMdd" );
        options.addOption( "c", "check", true, "check file. parameters: jstockCode-yyyyMMdd" );
        options.addOption( "u", "upload", true, "upload. parameters: jstockCode-yyyyMMdd" );
        options.addOption( "v", "upload2", true, "upload. parameters: jstockCode-yyyyMMdd" );
        options.addOption( "r", "clean", true, "clean. parameter: code" );
        options.addOption( "e", "exec", true, "exec. parameters: *-*-...." );
        options.addOption( "h", "help", false, "help." );
        options.addOption( "l", "list", false, "list." );

        String lineStr = null;
        BufferedReader br = null;
        if (run) {
            InputStreamReader isr = new InputStreamReader(System.in);
            br = new BufferedReader(isr);
        } else {
            br = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(cmd.getBytes())));
        }
        while (!((lineStr = br.readLine()) == null)) {

            Date startTime = new Date();
            Command command = new Command();
            command.setLineStr(lineStr);
            command.setTime(startTime);
            list.add(command);

            try {

                CommandLine line = parser.parse( options, lineStr.split(" "));

                if( line.hasOption( "a" ) ) {
                    jms.start();
                    log.info("start mq consumer");
                }
                if( line.hasOption( "b" ) ) {
                    jms.stop();
                    log.info("stop mq consumer");
                }
                if( line.hasOption( "x" ) ) {
                    command.setStockLists(
                            Shell.getTimeList(line.getOptionValue("x").split("-")[0], line.getOptionValue("x").split("-")[1]));
                    log.info(String.format("get time list: %s", command.getStockLists()));
                }
                if( line.hasOption( "y" ) ) {
                    command.setCodes(
                            Shell.getJstockCodeList(line.getOptionValue("y")));
                    log.info(String.format("get codes [%d]: ", command.getCodes().size(), command.getCodes().size()<30?command.getCodes():"*******"));
                }
                if( line.hasOption( "d" ) ) {
                    command.setStep("d");
                    command.setStepTime(new Date());
                    if (line.getOptionValue("d").equalsIgnoreCase("all")) {
                        for(StockList sl: command.getStockLists()) {
                            Shell.download(sl.getFolderOrFile());
                        }
                    } else {
                        Shell.download(line.getOptionValue("d"));
                    }
                    log.info("download");
                }
                if( line.hasOption( "t" ) ) {
                    command.setStep("t");
                    command.setStepTime(new Date());
                    if (line.getOptionValue("t").equalsIgnoreCase("all")) {
                        List<String> codeList = new ArrayList<>();
                        for (String code: command.getCodes()) {
                            codeList.add(code);
                            if (codeList.size()%300==0) {
                                for (StockList sl : command.getStockLists()) {
                                    Shell.toFile(codeList, sdf2.format(sl.getDate()));
                                }
                                codeList.clear();
                            }
                        }
                        if (!codeList.isEmpty()) {
                            for (StockList sl : command.getStockLists()) {
                                Shell.toFile(codeList, sdf2.format(sl.getDate()));
                            }
                        }
                    } else {
                        Shell.toFile(line.getOptionValue("t").split("-")[0], line.getOptionValue("t").split("-")[1]);
                    }
                    log.info("to file");
                }
                if( line.hasOption( "p" ) ) {
                    command.setStep("p");
                    command.setStepTime(new Date());
                    if (line.getOptionValue("p").equalsIgnoreCase("getVolume")) {
                        for (StockList sl : command.getStockLists()) {
                            JstockProcess.getVolume(command.getCodes(), sdf2.format(sl.getDate()));
                        }
                    } else {
                        log.info(String.format("unknow parameter %s", line.getOptionValue("p")));
                    }
                    log.info("process");
                }
                if( line.hasOption( "c" ) ) {
                    command.setStep("c");
                    command.setStepTime(new Date());
                    Shell.exists(line.getOptionValue("c").split("-")[0], line.getOptionValue("c").split("-")[1]);
                    log.info("check");
                }
                if( line.hasOption( "u" ) ) {
                    command.setStep("u");
                    command.setStepTime(new Date());
                    if (line.getOptionValue("u").equalsIgnoreCase("all")) {
                        for (String code: command.getCodes()) {
                            Shell.upload(code);
                        }
                    } else {
                        Shell.upload(line.getOptionValue("u").split("-")[0]);
                    }
                    log.info("upload");
                }
                if( line.hasOption( "v" ) ) {
                    command.setStep("v");
                    command.setStepTime(new Date());
                    if (line.getOptionValue("v").equalsIgnoreCase("all")) {
                        Shell.upload();
                    } else {
                        Shell.upload(line.getOptionValue("u").split("-")[0]);
                    }
                    log.info("upload");
                }
                if( line.hasOption( "r" ) ) {
                    command.setStep("r");
                    command.setStepTime(new Date());
                    Shell.clean();
                    log.info("clean");
                }
                if( line.hasOption( "e" ) ) {
                    Shell.exec(line.getOptionValue("e"));
                    log.info("exec");
                }
                if (line.hasOption( "h")) {
                    System.out.println(String.format("arg list"));
                    for (Option o: options.getOptions()) {
                        System.out.println(String.format("\t %s(%s) = %s", o.getOpt(), o.getLongOpt(), o.getDescription()));
                    }
                }
                if( line.hasOption( "l" ) ) {
                    log.info(om.writeValueAsString(command.getStockLists()));
                    log.info(om.writeValueAsString(command.getCodes()));
                }
            }
            catch( ParseException exp ) {
                exp.printStackTrace();
                log.info("Unexpected ParseException:" + exp.getMessage());
            }
            catch(Throwable t) {
                t.printStackTrace();
                log.info("Unexpected Throwable:" + t.getMessage());
            }

            Date endTime = new Date();

            command.setStep("end");
            command.setStepTime(new Date());
            command.setEnd(String.format("运行时间：%s -> %s，耗时：%d 分钟，命令：%s", sdf.format(startTime), sdf.format(endTime), (endTime.getTime() - startTime.getTime())/1000/60, lineStr ));
            log.info(command.getEnd());

            if (!run) break;
        }

        br.close();
    }

    void schedulingJobs(Scheduler sched, Class startClass, String startCommand, Class endClass, String endCommand) throws SchedulerException {
        this.log.info("------- Scheduling Jobs ----------------");
        JobDetail job = null;
        CronTrigger trigger = null;
        job = JobBuilder.newJob(startClass).build();
        trigger = (CronTrigger)TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(startCommand)).build();
        Date ft = sched.scheduleJob(job, trigger);
        this.log.info(job.getKey() + " has been scheduled to run at: " + ft + " and repeat based on expression: " + trigger.getCronExpression());
        job = JobBuilder.newJob(endClass).build();
        trigger = (CronTrigger)TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(endCommand)).build();
        ft = sched.scheduleJob(job, trigger);
        this.log.info(job.getKey() + " has been scheduled to run at: " + ft + " and repeat based on expression: " + trigger.getCronExpression());
    }

    public List<Command> getList() {
        return list;
    }

    public void setList(List<Command> list) {
        this.list = list;
    }

    @Data
    public class Command {

        List<StockList> stockLists = null;
        List<String> codes = null;
        String lineStr;
        Date time;
        String step;
        Date stepTime;
        String end;
    }

    public void schedulingJob(Scheduler sched, Class jobClass, String startCommand) throws SchedulerException {
        this.log.info("------- Scheduling Job ----------------");
        JobDetail job = null;
        CronTrigger trigger = null;
        job = JobBuilder.newJob(jobClass).build();
        trigger = (CronTrigger)TriggerBuilder.newTrigger().withSchedule(CronScheduleBuilder.cronSchedule(startCommand)).build();
        Date ft = sched.scheduleJob(job, trigger);
        this.log.info(job.getKey() + " has been scheduled to run at: " + ft + " and repeat based on expression: " + trigger.getCronExpression());
    }

    public static void main(String[] args) {
        System.out.println(200%100);
    }
}
