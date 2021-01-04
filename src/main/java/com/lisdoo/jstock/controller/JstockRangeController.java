package com.lisdoo.jstock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lisdoo.jstock.controller.utils.Result;
import com.lisdoo.jstock.controller.utils.ResultUtil;
import com.lisdoo.jstock.factory.MqConsumeFactory;
import com.lisdoo.jstock.service.exchange.*;
import com.lisdoo.jstock.service.exchange.exception.db.EntityExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/jr")
public class JstockRangeController {

    @Autowired
    JstockRepository jr;

    @Autowired
    JstockRangeService jrs;

    @Autowired
    JstockRangeRepository jrr;

    @GetMapping("/test")
    public Result<JstockRange> test() throws JsonProcessingException {

        Jstock j = jr.findByCode("000089").get();
        JstockRange jr = new JstockRange(j, 0f,null, 0, 0f, 0f, null, null, null, null, null, null,null, null);

        try {
            return ResultUtil.success(jrs.createJstockRange(jr));
        } catch (EntityExistException e) {
            return ResultUtil.error(e);
        }
    }
}
