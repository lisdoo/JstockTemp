package com.lisdoo.jstock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lisdoo.jstock.factory.MqConsumeFactory;
import com.lisdoo.jstock.service.exchange.JstockRangeRecordRepository;
import com.lisdoo.jstock.service.exchange.JstockRangeRepository;
import com.lisdoo.jstock.service.exchange.JstockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/env")
public class Env {

    @Autowired
    JstockRepository j;

    @Autowired
    JstockRangeRepository jr;

    @Autowired
    JstockRangeRecordRepository jrr;

    ObjectMapper om = new ObjectMapper();

    @GetMapping("/mq")
    public Set<String> mq() throws JsonProcessingException {

        return MqConsumeFactory.containers.keySet();
    }
}
