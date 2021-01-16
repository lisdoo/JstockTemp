package com.lisdoo.jstock.cli;

import com.alibaba.fastjson.JSONArray;
import com.lisdoo.jstock.readwrite.Data;
import com.lisdoo.jstock.readwrite.Filter;
import com.lisdoo.jstock.readwrite.Read;
import com.lisdoo.jstock.readwrite.Write;
import com.lisdoo.jstock.service.exchange.exception.JstockCodeNotExistException;
import com.lisdoo.jstock.service.exchange.exception.NotInTheTradingCycle;
import lombok.SneakyThrows;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Shell {

    private static final Log log = LogFactory.getLog(Shell.class);

    private static Pattern r = Pattern.compile("([0-9]{4}-[0-9]{2}-[0-9]{2})");
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private static SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMdd");

    public static void main(String[] args) throws IOException, InterruptedException, JstockCodeNotExistException, ParseException {

//        List list = getTimeList("20210111", "20210110");
//        System.out.println(
//                new ObjectMapper().writeValueAsString(list.size()));
//        System.out.println(
//                new ObjectMapper().writeValueAsString(list));

//        List list = getJstockCodeList("0000.*");
//        System.out.println(
//                new ObjectMapper().writeValueAsString(list.size()));

        Date startDate = sdf.parse("2021-01-11");
        Date endDate = sdf.parse("2021-01-11");
        Date test = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2021-01-11 10:00:00");
        endDate.setTime(endDate.getTime()+3600*24*1000);
        System.out.println(test.after(startDate));
        System.out.println(test.before(endDate));
        System.out.println(test.after(startDate) && test.before(endDate));
    }


    public static List<StockList> getTimeList(String startDateStr, String endDateStr) throws IOException, InterruptedException, ParseException {

        Date startDate = sdf2.parse(startDateStr);
        startDate.setTime(startDate.getTime()-3600*24*1000);
        Date endDate = sdf2.parse(endDateStr);
        endDate.setTime(endDate.getTime()+3600*24*1000);

        List<StockList> stockList = new ArrayList<>();

//        File path = new File("./jstocklist.txt");
        File path = new File("./temp");

        String cmd = "bypy list /jstocklog";

        Process process;
        try {
            log.info(String.format("cmd: %s", cmd));
            String[] envp = new String[] {
                    "LANG=en_US.utf8",
                    "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin:/opt/jdk/jdk1.8.0_221/bin:/sbin:/bin",
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

            process = Runtime.getRuntime().exec(cmd, envp, path);
//              BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader errReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            int exitValue = process.waitFor();

            Runnable runReader = new Runnable() {

                @SneakyThrows
                @Override
                public void run() {
                    String line;
                    while((line = reader.readLine())!= null){
                        Matcher m = r.matcher(line);
                        if (m.find()) {
                            boolean isfolderOrFile = line.split(" ")[0].equalsIgnoreCase("d");
                            String folderOrFile = line.split(" ")[1];
                            stockList.add(new StockList(line, isfolderOrFile, folderOrFile, sdf.parse(m.group(0))));
                        } else {
                            System.out.println("NO MATCH");
                        }
                    }
                    if (exitValue == 0){
                        log.info("successfully executed the linux command");
                    } else {
                        log.info(String.format("exit with error code: %d", exitValue));
                        throw new InterruptedException();
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

            Thread t1 = new Thread(runReader);
            new Thread(runErrReader).start();

            t1.start();

            while(t1.isAlive()) {
                Thread.sleep(300);
            }
            if(exitValue != 0) {
                throw new InterruptedException();
            }
            List<StockList> forReturn = stockList.stream().filter(p->{
                return p.date.after(startDate) && p.date.before(endDate);
            }).collect(Collectors.toList());

            return forReturn;

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static List<String> getJstockCodeList(String jstockCode) throws IOException, JstockCodeNotExistException {

        List<String> stockCodeList = new ArrayList<>();

        File path = new File("./stocksInWindFormat/stocks");

        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));

        String line;
        while((line = reader.readLine())!= null){

            String[] codes = line.split(",");

            for (String code: codes) {
                stockCodeList.add(code.replace("sse", "" ).replace("szse", "" ));
            }
        }

        Pattern r = Pattern.compile(jstockCode);
        stockCodeList = stockCodeList.stream().filter(p -> {
            return r.matcher(p).find();
        }).collect(Collectors.toList());
        if (stockCodeList.isEmpty()) {
            throw new JstockCodeNotExistException();
        }
        return stockCodeList;
    }

    public static void download(String fileOrFolder) throws IOException, InterruptedException {

        File path = new File("./temp");
        if (!path.exists()) path.mkdir();
        log.info(path.getAbsolutePath());

        Process process;
        try {
            String cmd = String.format("%s %s/download.sh %s", "bash", path.getParentFile().getAbsolutePath(), fileOrFolder);
            log.info(String.format("cmd: %s", cmd));
            String[] envp = new String[] {
                    "LANG=en_US.utf8",
                    "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin:/opt/jdk/jdk1.8.0_221/bin:/sbin:/bin",
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

            Thread t1 = new Thread(runReader);
            new Thread(runErrReader).start();

            t1.start();

            while(t1.isAlive()) {
                Thread.sleep(300);
            }
            if(exitValue != 0) {
                throw new InterruptedException();
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void toFile(String jstockCode, String yyyyMMdd) throws Exception, NotInTheTradingCycle {

        File path = new File("./temp");
        File toFile = new File("./newfile");
        toFile.mkdir();

        File toPath = new File(toFile, jstockCode);
        toPath.mkdir();

        Write w = new Write(toPath.getAbsolutePath(), yyyyMMdd, true);

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
        w.close();
    }

    public static void toFile(List<String> jstockCodes, String yyyyMMdd) throws Exception, NotInTheTradingCycle {

        File path = new File("./temp");
        File toFile = new File("./tofile");
        toFile.mkdir();

        Map<String, Write> map = new HashMap<>();

        for (String jstockCode: jstockCodes) {
            File toPath = new File(toFile, jstockCode);
            toPath.mkdir();
            Write w = new Write(toPath.getAbsolutePath(), yyyyMMdd, true);
            map.put(jstockCode, w);
        }

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

                for (Map.Entry<String, Write> entry: map.entrySet()) {
                    /*
                     * 跳过异常值
                     */
                    if (!Filter.check(entry.getKey(), data)) {
                        continue;
                    }

                    if (entry.getKey().equalsIgnoreCase(data.getCode())) {
                        entry.getValue().put(o);
                    }
                }
                return true;
            }
        };

        String[] files = path.list((f1, f2)->{
            return !f2.contains(".xz");
        });
        for (String file: files) {
            log.info(String.format("reading file %s to file %s", file, map.keySet()));
            Read.testRead(new File(path.getAbsolutePath(), file).getAbsolutePath(), p);
        }
        for (Write w: map.values()) {
            w.close();
        }
    }

    public static void clean() throws IOException, InterruptedException {

        File path = new File("./temp");
        if (!path.exists()) path.mkdir();

        Process process;
        try {
            String cmd = String.format("%s %s/rm.sh stocksinfo* %s", "sh", path.getParentFile().getAbsolutePath(), "tofile");
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
                throw new InterruptedException();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void exec(String cmd) throws IOException, InterruptedException {

        File path = new File("./temp");

        cmd = cmd.replace('-', ' ');

        Process process;
        try {
            log.info(String.format("cmd: %s", cmd));
            String[] envp = new String[] {
                    "LANG=en_US.utf8",
                    "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin:/opt/jdk/jdk1.8.0_221/bin:/sbin:/bin",
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
                        throw new InterruptedException();
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


            Thread t1 = new Thread(runReader);
            new Thread(runErrReader).start();

            t1.start();

            while(t1.isAlive()) {
                Thread.sleep(300);
            }
            if(exitValue != 0) {
                throw new InterruptedException();
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void mkdir(String code) throws IOException, InterruptedException {

        File path = new File("./temp");

        String cmd = String.format("bypy mkdir /jstockcodelog/%s", code);

        Process process;
        try {
            log.info(cmd);
            String[] envp = new String[] {
                    "LANG=en_US.utf8",
                    "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin:/opt/jdk/jdk1.8.0_221/bin:/sbin:/bin",
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
                        throw new InterruptedException();
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


            Thread t1 = new Thread(runReader);
            new Thread(runErrReader).start();

            t1.start();

            while(t1.isAlive()) {
                Thread.sleep(300);
            }
            if(exitValue != 0) {
                throw new InterruptedException();
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void upload(String code) throws IOException, InterruptedException {

        File path = new File("./temp");

        String cmd = String.format("bypy upload ../%s /jstockcodelog/%s -r 10", code, code);

        Process process;
        try {
            log.info(cmd);
            String[] envp = new String[] {
                    "LANG=en_US.utf8",
                    "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin:/opt/jdk/jdk1.8.0_221/bin:/sbin:/bin",
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
                        throw new InterruptedException();
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

            Thread t1 = new Thread(runReader);
            new Thread(runErrReader).start();

            t1.start();

            while(t1.isAlive()) {
                Thread.sleep(300);
            }
            if(exitValue != 0) {
                throw new InterruptedException();
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void upload() throws IOException, InterruptedException {

        File path = new File("./temp");

        String cmd = String.format("bypy upload ../tofile /jstockcodelog -r 10");

        Process process;
        try {
            log.info(cmd);
            String[] envp = new String[] {
                    "LANG=en_US.utf8",
                    "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin:/opt/jdk/jdk1.8.0_221/bin:/sbin:/bin",
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
                        throw new InterruptedException();
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

            Thread t1 = new Thread(runReader);
            new Thread(runErrReader).start();

            t1.start();

            while(t1.isAlive()) {
                Thread.sleep(300);
            }
            if(exitValue != 0) {
                throw new InterruptedException();
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static boolean exists(String code, String yyyyMMdd) throws IOException, InterruptedException {

        List<Boolean> list = new ArrayList<>();

        File path = new File("./temp");

        String cmd = String.format("bypy list jstockcodelog/%s/%s", code, yyyyMMdd);

        Process process;
        try {
            log.info(cmd);
            String[] envp = new String[] {
                    "LANG=en_US.utf8",
                    "PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/root/bin:/opt/jdk/jdk1.8.0_221/bin:/sbin:/bin",
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
                        if (line.contains(code) && line.contains(yyyyMMdd)) {
                            list.add(true);
                        }
                        log.info(line);
                    }
                    if (exitValue == 0){
                        log.info("successfully executed the linux command");
                    } else {
                        log.info(String.format("exit with error code: %d", exitValue));
                        throw new InterruptedException();
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

            Thread t1 = new Thread(runReader);
            new Thread(runErrReader).start();

            t1.start();

            while(t1.isAlive()) {
                Thread.sleep(300);
            }
            if(exitValue != 0) {
                throw new InterruptedException();
            }

            if (list.isEmpty()) {
                return false;
            } else {
                return true;
            }

        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
            throw e;
        }
    }
}
