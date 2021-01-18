package com.lisdoo.jstock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lisdoo.jstock.controller.utils.Result;
import com.lisdoo.jstock.controller.utils.ResultUtil;
import com.lisdoo.jstock.service.exchange.*;
import com.lisdoo.jstock.service.exchange.exception.db.EntityExistException;
import com.lisdoo.jstock.service.vi.JstockV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
        JstockRange jr = new JstockRange(j, 0f,null, 0, 0f, 0f, null, null, null, 0f, null, null, null, null,null, null);

        try {
            return ResultUtil.success(jrs.createJstockRange(jr));
        } catch (EntityExistException e) {
            return ResultUtil.error(e);
        }
    }

    @GetMapping("/jstock")
    public Result<List<JstockV>> all() throws JsonProcessingException {

        List<JstockV> o = jr.findAllJstocks(getRequest().getRequestURL().toString());

        return ResultUtil.success( o);

    }

    private HttpServletRequest getRequest() {

        HttpServletRequest request = null;
        try {
            request =
                    ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return request;
    }
}
