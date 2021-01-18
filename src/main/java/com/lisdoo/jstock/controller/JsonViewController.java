package com.lisdoo.jstock.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.lisdoo.jstock.controller.utils.Result;
import com.lisdoo.jstock.controller.utils.ResultUtil;
import com.lisdoo.jstock.service.exchange.*;
import com.lisdoo.jstock.service.vi.JstockV;
import com.lisdoo.jstock.service.vi.SpecJsRecV;
import com.lisdoo.jstock.service.vi.SpecJsV;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/view")
public class JsonViewController {

    @Autowired
    JstockRepository jr;

    @Autowired
    JstockRangeService jrs;

    @Autowired
    JstockRangeRepository jrr;

    @GetMapping("/jstock")
    public Result<List<JstockV>> all() throws JsonProcessingException {

        List<JstockV> o = jr.findAllJstocks(getRequest().getRequestURL().toString());

        return ResultUtil.success( o);

    }

    @GetMapping("/jstock/{code}")
    public Result<List<SpecJsV>> specJs(@PathVariable String code) throws JsonProcessingException {

        List<SpecJsV> o = jr.findSpecJs(getRequest().getRequestURL().toString(), code);

        return ResultUtil.success( o);

    }

    @GetMapping("/jstock/{code}/{strategyId}")
    public Result<List<SpecJsRecV>> findRangRec(@PathVariable String code, @PathVariable Long strategyId) throws JsonProcessingException {

        List<SpecJsRecV> o = jr.findRangRec(code, strategyId);

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
