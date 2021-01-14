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

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class JstockProcess {

    private static final Log log = LogFactory.getLog(JstockProcess.class);

    public static void getVolume(List<String> jstockCodes, String yyyyMMdd) throws Exception, NotInTheTradingCycle {

        File path = new File("./temp");

        Map<String, Long[]> map = new HashMap<>();

        File toPath = new File("process");
        toPath.mkdir();
        if (new File(toPath.getAbsolutePath(), yyyyMMdd).exists()) {
            new File(toPath.getAbsolutePath(), yyyyMMdd).delete();
        }
        Write w = new Write(toPath.getAbsolutePath(), yyyyMMdd, true);

        for (String jstockCode: jstockCodes) {
            Long[] l = new Long[2];
            l[0] = 0l;
            l[1] = 0l;
            map.put(jstockCode, l);
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

                for (Map.Entry<String, Long[]> entry: map.entrySet()) {
                    /*
                     * 跳过异常值
                     */
                    if (!Filter.check(entry.getKey(), data)) {
                        continue;
                    }

                    if (entry.getKey().equalsIgnoreCase(data.getCode())) {
                        // Volume
                        if (data.getVolume() > entry.getValue()[0]) {
                            entry.getValue()[0] = data.getVolume();
                            entry.setValue(entry.getValue());
                        }
                        // Amount
                        if (data.getAmount() > entry.getValue()[1]) {
                            entry.getValue()[1] = data.getAmount();
                            entry.setValue(entry.getValue());
                        }
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

        w.write("code,volume,amount\r\n");
        for (Map.Entry<String, Long[]> entry: map.entrySet()) {
            w.write(entry.getKey());
            w.write(",");
            w.write(Long.toString(entry.getValue()[0]));
            w.write(",");
            w.write(Long.toString(entry.getValue()[1]));
            w.write("\r\n");
        }

        w.close();
    }
}
