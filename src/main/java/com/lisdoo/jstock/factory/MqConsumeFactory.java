package com.lisdoo.jstock.factory;

import com.lisdoo.jstock.SpringContextHolder;
import com.lisdoo.jstock.service.mqhandler.JstockConsumeHandler;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MqConsumeFactory {

    public static Map<String, MySimpleMessageListenerContainer> containers = new HashMap<>();
    static CachingConnectionFactory connectionFactory;
    public static final String PATTEN_DEFAULT_YMD = "yyyy-MM-dd";

    public MqConsumeFactory() {
    }

    public static synchronized SimpleMessageListenerContainer get(String code) {

        if (MqConsumeFactory.connectionFactory == null) {
            CachingConnectionFactory connectionFactory = new CachingConnectionFactory("ali47");
            connectionFactory.setUsername("guest");
            connectionFactory.setPassword("lenovo.112");
            MqConsumeFactory.connectionFactory = connectionFactory;
        }

        if (!containers.containsKey(code)) {

            Queue queue = new Queue(code);

            JstockConsumeHandler jch = (JstockConsumeHandler) SpringContextHolder.getBean("jstockConsumeHandler");
            jch.setJstockCode(queue.getName());

            MySimpleMessageListenerContainer container = new MySimpleMessageListenerContainer();
            container.setConnectionFactory(connectionFactory);
            container.setQueueNames(queue.getName());
            container.setMessageListener(new MessageListenerAdapter(jch), jch);

            containers.put(queue.getName(), container);

            return container;
        } else {
            return MqConsumeFactory.containers.get(code);
        }
    }

    public static synchronized void release() {

        for (SimpleMessageListenerContainer container : containers.values()) {
            container.stop();
        }
        connectionFactory.destroy();
        containers.clear();
        connectionFactory = null;
    }

    public static void startAll() {

        for (SimpleMessageListenerContainer container : MqConsumeFactory.containers.values()) {
            container.start();
        }
    }

    public static void stopAll() {

        for (SimpleMessageListenerContainer container : MqConsumeFactory.containers.values()) {
            container.stop();
        }
    }

    public static String getInfo(String stockCode) {

        SimpleDateFormat sf = new SimpleDateFormat(PATTEN_DEFAULT_YMD);

        for (MySimpleMessageListenerContainer container : MqConsumeFactory.containers.values()) {

            if (container.getJch().getJstockCode().equalsIgnoreCase(stockCode)) {

                //获取今天的日期
                String nowDay = sf.format(new Date());
                //对比的时间
                String day = sf.format(container.getJch().getStartDate());
                if (day.equals(nowDay)) {
                    return String.format("今天处理量：%d", container.getJch().getCount());
                } else {
                    return String.format("%s的处理量:%d", day, container.getJch().getCount());
                }
            }
        }

        return String.format("未查找到当前%s的消息处理对象", stockCode);
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        MqConsumeFactory.get("000089");
        MqConsumeFactory.get("002180");

        MqConsumeFactory.startAll();
//        Thread.sleep(10000);

//
        MqConsumeFactory.release();
    }
}
