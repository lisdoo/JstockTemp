package com.lisdoo.jstock.exchange;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class JstockRepositoryTests {

    @Autowired
    JstockRepository repository;

    @Before
    public void setUp() {

        repository.save(new Jstock("01", "一", "", new Date(), new Date()));
        repository.save(new Jstock("02", "一", "", new Date(), new Date()));
        repository.save(new Jstock("03", "一", "", new Date(), new Date()));
        repository.save(new Jstock("04", "一", "", new Date(), new Date()));
        repository.save(new Jstock("05", "一", "", new Date(), new Date()));
        repository.save(new Jstock("06", "一", "", new Date(), new Date()));
    }

    @Test
    public void test() {

    }
}
