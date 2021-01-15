package com.lisdoo.jstock.health;

import com.lisdoo.jstock.cli.CmdRunner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;

@Component
public class CliHealthIndicator extends AbstractHealthIndicator {

    @Autowired
    CmdRunner cmdRunner;

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {

        builder.up()
                .withDetail("codes", cmdRunner.getCodes()==null?"":"")
                .withDetail("stockLists", cmdRunner.getStockLists()==null?"":"");
    }
}
