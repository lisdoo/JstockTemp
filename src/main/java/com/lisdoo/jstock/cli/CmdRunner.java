package com.lisdoo.jstock.cli;

import antlr.InputBuffer;
import com.lisdoo.jstock.service.exchange.JstockRangeRecordRepository;
import com.lisdoo.jstock.service.exchange.JstockRangeRepository;
import com.lisdoo.jstock.service.exchange.JstockRepository;
import com.lisdoo.jstock.service.exchange.JstockStrategy;
import org.apache.commons.cli.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;

@Component
public class CmdRunner implements CommandLineRunner {

    @Autowired
    JstockRepository j;

    @Autowired
    JstockRangeRepository jr;

    @Autowired
    JstockRangeRecordRepository jrr;

    @Override
    public void run(String... args) throws Exception {
        // create the command line parser
        CommandLineParser parser = new DefaultParser();

        // create the Options
        Options options = new Options();
        options.addOption( "a", "all", true, "do not hide entries starting with ." );
        options.addOption( "A", "almost-all", false, "do not list implied . and .." );
        options.addOption( "b", "escape", false, "print octal escapes for nongraphic "
                + "characters" );
        options.addOption( OptionBuilder.withLongOpt( "block-size" )
                .withDescription( "use SIZE-byte blocks" )
                .hasArg()
                .withArgName("SIZE")
                .create() );
        options.addOption( "B", "ignore-backups", false, "do not list implied entried "
                + "ending with ~");
        options.addOption( "c", false, "with -lt: sort by, and show, ctime (time of last "
                + "modification of file status information) with "
                + "-l:show ctime and sort by name otherwise: sort "
                + "by ctime" );
        options.addOption( "C", false, "list entries by columns" );

//        String[] args = new String[]{ "--block-size=10" };


        String lineStr = null;
        InputStreamReader isr = new InputStreamReader(System.in);
        BufferedReader br = new BufferedReader(isr);
        while (!(lineStr = br.readLine()).isEmpty()) {

            try {

                CommandLine line = parser.parse( options, lineStr.split(" "));

                if( line.hasOption( "block-size" ) ) {
                    System.out.println( "\t"+line.getOptionValue( "block-size" ) );
                }
                if( line.hasOption( "a" ) ) {
                    System.out.println( "\t"+line.getOptionValue( "a" ) );
                }
                System.out.println("bbbbbbbbbbbbbbbbb"+j);
            }
            catch( ParseException exp ) {
                System.out.println( "Unexpected exception:" + exp.getMessage() );
            }
        }

        System.out.println("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
    }

    private static void printHelp(Options options) {
        String cmdLineSyntax =
                "CorpusTool [OPTIONS] <INFILE>";

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(cmdLineSyntax, options, false);
    }
}
