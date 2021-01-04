package com.lisdoo.jstock.service.exchange;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Transactional
public class JstockRepositoryTests {

    @Autowired
    JstockRepository repository;

    @Autowired
    JstockRangeRepository jstockRangeRepository;

    @Before
    public void setUp() {

        repository.save(new Jstock("01", "一", "", null, new Date(), new Date()));
        repository.save(new Jstock("02", "一", "", null, new Date(), new Date()));
        repository.save(new Jstock("03", "一", "", null, new Date(), new Date()));
        repository.save(new Jstock("04", "一", "", null, new Date(), new Date()));
        repository.save(new Jstock("05", "一", "", null, new Date(), new Date()));
        repository.save(new Jstock("06", "一", "", null, new Date(), new Date()));
    }

    @Test
    public void test() throws JsonProcessingException {

        ObjectMapper om = new ObjectMapper();

        Optional<Jstock> j = repository.findByCode("000089");
        System.out.println(om.writeValueAsString(j.get()));
        j = repository.findById(1L);
        System.out.println(om.writeValueAsString(j.get()));

        Optional<JstockRange> jr = jstockRangeRepository.findById(1l);
        System.out.println(om.writeValueAsString(jr.get()));
    }
}
