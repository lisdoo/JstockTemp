package com.lisdoo.jstock.health;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lisdoo.jstock.cli.CmdRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;

@Component
public class CliHealthIndicator extends AbstractHealthIndicator {

    @Autowired
    CmdRunner cmdRunner;

    @Autowired
    ObjectMapper om;

    private static SimpleDateFormat sdf =  new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        Health.Builder b = builder.up();

        for (CmdRunner.Command command: cmdRunner.getList()) {
            b.withDetail(sdf.format(command.getTime()), command);
        }
    }
}
