package com.lisdoo.jstock.cli;

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
import java.util.Date;

@Component
public class CmdRunner implements CommandLineRunner {

    private static final Log log = LogFactory.getLog(CmdRunner.class);

    @Autowired
    JstockMqService jms;

    @Override
    public void run(String... args) throws Exception {


        this.log.info("------- Initializing -------------------");
        SchedulerFactory sf = new StdSchedulerFactory();
        Scheduler sched = sf.getScheduler();
        this.log.info("------- Initialization Complete --------");

        this.schedulingJobs(sched, StartThreads.class, "0 20 9 ? * MON-FRI", ShutdownThreads.class, "0 1 15 ? * MON-FRI");

        this.log.info("------- Starting Scheduler ----------------");
        sched.start();
        this.log.info("------- Started Scheduler -----------------");

        Runnable r = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {

                // create the command line parser
                CommandLineParser parser = new DefaultParser();

                // create the Options
                Options options = new Options();
                options.addOption( "a", "start", false, "Start." );
                options.addOption( "b", "stop", false, "Stop." );
                options.addOption( "d", "download", true, "download." );
                options.addOption( "t", "to file", true, "to file." );
                options.addOption( "r", "rm", true, "rm." );

                String lineStr = null;
                InputStreamReader isr = new InputStreamReader(System.in);
                BufferedReader br = new BufferedReader(isr);
                while (!(lineStr = br.readLine()).isEmpty()) {

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
                        if( line.hasOption( "d" ) ) {
                            DownloadShell.sh(line.getOptionValue("d"));
                            log.info("download");
                        }
                        if( line.hasOption( "t" ) ) {
                            DownloadShell.toFile(line.getOptionValue("t"));
                            log.info("to file");
                        }
                        if( line.hasOption( "r" ) ) {
                            DownloadShell.rm(line.getOptionValue("r"));
                            log.info("rm");
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
