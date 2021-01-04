package com.lisdoo.jstock;

import com.lisdoo.jstock.factory.MqConsumeFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class MqConsumeFactoryTest {

    @Test
    public void test() throws IOException, InterruptedException {

        MqConsumeFactory.get("000089");

        for (SimpleMessageListenerContainer container: MqConsumeFactory.containers.values()) {
            container.start();
        }

        Thread.sleep(100*1000);

        MqConsumeFactory.release();
    }
}