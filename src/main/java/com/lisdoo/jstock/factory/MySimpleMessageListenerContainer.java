package com.lisdoo.jstock.factory;

import com.lisdoo.jstock.service.mqhandler.JstockConsumeHandler;
import org.springframework.amqp.core.MessageListener;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;

public class MySimpleMessageListenerContainer extends SimpleMessageListenerContainer {

    JstockConsumeHandler jch;

    public MySimpleMessageListenerContainer() {
    }

    public MySimpleMessageListenerContainer(ConnectionFactory connectionFactory) {
        this.setConnectionFactory(connectionFactory);
    }

    public void setMessageListener(MessageListener messageListener, JstockConsumeHandler jch) {
        this.jch = jch;
        super.setMessageListener(messageListener);
    }

    public JstockConsumeHandler getJch() {
        return jch;
    }

    public void setJch(JstockConsumeHandler jch) {
        this.jch = jch;
    }
}
