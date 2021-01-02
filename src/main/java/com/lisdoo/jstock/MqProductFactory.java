package com.lisdoo.jstock;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import java.util.HashMap;
import java.util.Map;

public class MqProductFactory {

    public static Map<String, RabbitTemplate> templates = new HashMap<>();
    static CachingConnectionFactory connectionFactory;

    public MqProductFactory() {
    }

    public static synchronized RabbitTemplate get(String code) {

        if (MqProductFactory.connectionFactory == null) {
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory("ali47");
            connectionFactory.setUsername("guest");
            connectionFactory.setPassword("lenovo.112");
            MqProductFactory.connectionFactory = connectionFactory;
        }

        if (!templates.containsKey(code)) {
            RabbitAdmin ra = new RabbitAdmin(connectionFactory);
            Queue queue = new Queue(code);
            ra.declareQueue(queue);

            RabbitTemplate template = new RabbitTemplate(connectionFactory);
            template.setRoutingKey(queue.getName());
            template.setDefaultReceiveQueue(queue.getName());

            templates.put(queue.getName(), template);

            return template;
        } else {
            return MqProductFactory.templates.get(code);
        }
    }

    public static synchronized void release() {

        for (RabbitTemplate template: templates.values()) {
            template.stop();
        }
        connectionFactory.destroy();
        templates.clear();
        connectionFactory = null;
    }
}

