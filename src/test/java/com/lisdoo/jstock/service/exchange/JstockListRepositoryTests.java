package com.lisdoo.jstock.service.exchange;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
@Rollback(false)
public class JstockListRepositoryTests {

    @Autowired
    JstockListRepository repository;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    @Before
    public void setUp() throws IOException, ParseException {

        Pattern r = Pattern.compile("([a-zA-Z]*)([0-9]*)");
        Date createDate = sdf.parse("2015-01-01");

        String line = null;

        FileInputStream fis = new FileInputStream(new File("./stocksInWindFormat/stocks"));
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader bufferedReader = new BufferedReader(isr);
        while ((line = bufferedReader.readLine()) != null) {
            String[] codes = line.split(",");
            for (String code: codes) {
                Matcher m = r.matcher(code);
                if (m.find()) {
                    System.out.println(String.format("%s=%s-%s", m.group(0), m.group(1), m.group(2)));
                    repository.save(new JstockList(m.group(2), m.group(1), "", createDate, null));
                }
            }
        }

    }

    @Test
    public void test() {

//        repository.save(new JstockList("02", "一", new Date(), new Date()));
//        repository.save(new JstockList("03", "一", new Date(), new Date()));
    }
}
