package com.lisdoo.jstock.cli;

import com.alibaba.fastjson.JSONArray;
import com.lisdoo.jstock.readwrite.Data;
import com.lisdoo.jstock.readwrite.Filter;
import com.lisdoo.jstock.readwrite.Read;
import com.lisdoo.jstock.readwrite.Write;
import com.lisdoo.jstock.service.exchange.exception.NotInTheTradingCycle;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.function.Predicate;

public class DownloadShell {

    private static final Log log = LogFactory.getLog(DownloadShell.class);

    public static void main(String[] args) {

        String fileOrFolder = "";
    }

    public static void sh(String fileOrFolder) {

        File path = new File("./temp");
        if (!path.exists()) path.mkdir();
        log.info(path.getAbsolutePath());

        Process process;
        try {
            String cmd = String.format("%s %s/download.sh %s", "sh", path.getParentFile().getAbsolutePath(), fileOrFolder);
            log.info(String.format("cmd: %s", cmd));
            String[] envp = new String[] {
                    "LANG=en_US.utf8",
                    "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin:/opt/jdk/jdk1.8.0_221/bin",
                    "LC_CTYPE=en_US.utf8",
                    "LC_NUMERIC=en_US.utf8",
                    "LC_TIME=en_US.utf8",
                    "LC_COLLATE=en_US.utf8",
                    "LC_MONETARY=en_US.utf8",
                    "LC_MESSAGES=en_US.utf8",
                    "LC_PAPER=en_US.utf8",
                    "LC_NAME=en_US.utf8",
                    "LC_ADDRESS=en_US.utf8",
                    "LC_TELEPHONE=en_US.utf8",
                    "LC_MEASUREMENT=en_US.utf8",
                    "LC_IDENTIFICATION=en_US.utf8"
            };
            /*
             * 以上环境变量设置后，还需要修改/usr/bin/bypy文件，如下：
             * #!/usr/bin/python2
             * # -*- coding: utf-8 -*-
             * import re
             * import sys
             * from bypy.bypy import main
             * reload(sys)
             * sys.setdefaultencoding('utf-8')
             * print sys.getdefaultencoding();
             * if __name__ == '__main__':
             *     sys.argv[0] = re.sub(r'(-script\.pyw|\.exe)?$', '', sys.argv[0])
             *     sys.exit(main())
             */
            process = Runtime.getRuntime().exec(cmd, envp, path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            int exitValue = process.waitFor();

            Runnable runReader = new Runnable() {

                @SneakyThrows
                @Override
                public void run() {
                    String line;
                    while((line = reader.readLine())!= null){
                        log.info(line);
                    }
                    if (exitValue == 0){
                        log.info("successfully executed the linux command");
                    } else {
                        log.info(String.format("exit with error code: %d", exitValue));
                    }
                }
            };

            Runnable runErrReader = new Runnable() {

                @SneakyThrows
                @Override
                public void run() {
                    String line;
                    while((line = errReader.readLine())!= null){
                        log.error(line);
                    }
                }
            };

            new Thread(runReader).start();
            new Thread(runErrReader).start();

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void toFile(String jstockCode) throws Exception, NotInTheTradingCycle {

        File path = new File("./temp");

        Write w = new Write(path.getParentFile().getAbsolutePath(), jstockCode, true);

        Predicate p = new Predicate<JSONArray>() {

            @SneakyThrows
            @Override
            public boolean test(JSONArray o) {

                Data data = null;
                try {
                    data = new Data(o);
                } catch (Exception e) {
                    e.printStackTrace();
                    return true;
                }

                /*
                 * 跳过异常值
                 */
                if (!Filter.check(jstockCode, data)) {
                    return true;
                }

                if (jstockCode.equalsIgnoreCase(data.getCode())) {
                    w.put(o);
                }
                return true;
            }
        };

        String[] files = path.list((f1, f2)->{
           return !f2.contains(".xz");
        });
        for (String file: files) {
            log.info(String.format("reading file %s to file %s", file, jstockCode));
            Read.testRead(new File(path.getAbsolutePath(), file).getAbsolutePath(), p);
        }
    }

    public static void rm(String fileOrFolder) {

        File path = new File("./temp");
        if (!path.exists()) path.mkdir();
        log.info(path.getAbsolutePath());

        Process process;
        try {
            String cmd = String.format("%s %s/rm.sh stocksinfo*", "sh", path.getParentFile().getAbsolutePath());
            log.info(String.format("cmd: %s", cmd));
            process = Runtime.getRuntime().exec(cmd, null, path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            int exitValue = process.waitFor();
            while((line = reader.readLine())!= null){
                log.info(line);
            }
            if (exitValue == 0){
                log.info("successfully executed the linux command");
            } else {
                log.info(String.format("exit with error code: %d", exitValue));
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }

}
