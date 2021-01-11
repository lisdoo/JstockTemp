package com.lisdoo.jstock.cli;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lisdoo.jstock.service.exchange.process.JstockMqService;
import lombok.SneakyThrows;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Component
public class CmdRunner implements CommandLineRunner {

    private static final Log log = LogFactory.getLog(CmdRunner.class);

    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

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

            this.log.info("------- Starting Scheduler ----------------");
            sched.start();
            this.log.info("------- Started Scheduler -----------------");
        }

        Runnable r = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {

                List<StockList> stockLists = null;
                List<String> codes = null;
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
                options.addOption( "c", "check", true, "check file. parameters: jstockCode-yyyyMMdd" );
                options.addOption( "u", "upload", true, "upload. parameters: jstockCode-yyyyMMdd" );
                options.addOption( "r", "clean", true, "clean. parameter: code" );
                options.addOption( "e", "exec", true, "exec. parameters: *-*-...." );
                options.addOption( "h", "help", false, "help." );
                options.addOption( "l", "list", false, "list." );

                String lineStr = null;
                InputStreamReader isr = new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader(isr);
                while (!((lineStr = br.readLine()) == null)) {

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
                            stockLists =
                                    Shell.getTimeList(line.getOptionValue("x").split("-")[0], line.getOptionValue("x").split("-")[1]);
                            log.info("get time list");
                        }
                        if( line.hasOption( "y" ) ) {
                            codes =
                                    Shell.getJstockCodeList(line.getOptionValue("y"));
                            log.info("get codes");
                        }
                        if( line.hasOption( "d" ) ) {
                            if (line.getOptionValue("d").equalsIgnoreCase("all")) {
                                for(StockList sl: stockLists) {
                                    Shell.download(sl.getFolderOrFile());
                                }
                            } else {
                                Shell.download(line.getOptionValue("d"));
                            }
                            log.info("download");
                        }
                        if( line.hasOption( "t" ) ) {
                            if (line.getOptionValue("t").equalsIgnoreCase("all")) {
                                for (String code: codes) {
                                    for(StockList sl: stockLists) {
                                        Shell.toFile(code, sdf2.format(sl.getDate()));
                                    }
                                }
                            } else {
                                Shell.toFile(line.getOptionValue("t").split("-")[0], line.getOptionValue("t").split("-")[1]);
                            }
                            log.info("to file");
                        }
                        if( line.hasOption( "c" ) ) {
                            Shell.exists(line.getOptionValue("c").split("-")[0], line.getOptionValue("c").split("-")[1]);
                            log.info("check");
                        }
                        if( line.hasOption( "u" ) ) {
                            if (line.getOptionValue("u").equalsIgnoreCase("all")) {
                                for (String code: codes) {
                                    for(StockList sl: stockLists) {
                                        Shell.upload(code, sdf2.format(sl.getDate()));
                                    }
                                }
                            } else {
                                Shell.upload(line.getOptionValue("u").split("-")[0], line.getOptionValue("u").split("-")[1]);
                            }
                            log.info("upload");
                        }
                        if( line.hasOption( "r" ) ) {
                            if (codes != null) {
                                for (String code : codes) {
                                    Shell.clean(code);
                                }
                            } else {
                                Shell.clean(line.getOptionValue("r"));
                            }
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
                            log.info(om.writeValueAsString(stockLists));
                            log.info(om.writeValueAsString(codes));
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
                }

            }
        };
        new Thread(r).start();
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
}
