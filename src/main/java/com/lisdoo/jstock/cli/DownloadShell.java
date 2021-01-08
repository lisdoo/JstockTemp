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
            process = Runtime.getRuntime().exec(cmd, null, path.getParentFile());//查看我的 .bash_history里面的grep 命令使用历史记录
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

    public static void toFile(String jstockCode) throws Exception, NotInTheTradingCycle {

        File path = new File("./temp");

        Write w = new Write(path.getAbsolutePath(), jstockCode);

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
           return !f1.getName().contains(".");
        });
        for (String file: files) {
            log.info(String.format("to file %s", file));
            Read.testRead(file, p);
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
            process = Runtime.getRuntime().exec(cmd, null, path.getParentFile());//查看我的 .bash_history里面的grep 命令使用历史记录
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
