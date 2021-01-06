package com.lisdoo.jstock.cli;

import antlr.InputBuffer;
import com.lisdoo.jstock.service.exchange.JstockRangeRecordRepository;
import com.lisdoo.jstock.service.exchange.JstockRangeRepository;
import com.lisdoo.jstock.service.exchange.JstockRepository;
import com.lisdoo.jstock.service.exchange.JstockStrategy;
import com.lisdoo.jstock.service.exchange.process.JstockMqService;
import lombok.SneakyThrows;
import org.apache.commons.cli.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;

@Component
public class CmdRunner implements CommandLineRunner {

    private static final Log log = LogFactory.getLog(CmdRunner.class);

    @Autowired
    JstockMqService jms;

    @Override
    public void run(String... args) throws Exception {

        Runnable r = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {

                // create the command line parser
                CommandLineParser parser = new DefaultParser();

                // create the Options
                Options options = new Options();
                options.addOption( "a", "start", false, "Start." );
                options.addOption( "b", "stop", false, "Start." );

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
}