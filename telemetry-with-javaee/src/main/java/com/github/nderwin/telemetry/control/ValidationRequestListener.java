/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.control;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.util.logging.Logger;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;

@Stateless
@LocalBean
public class ValidationRequestListener {

    private static final Logger LOG = Logger.getLogger(ValidationRequestListener.class.getName());
    
    private static final String EXCHANGE_NAME = "address-verify";
    
    private static final String ROUTING_KEY = "";
    
    ConnectionFactory factory;

    public ValidationRequestListener() {
        factory = new ConnectionFactory();
        factory.setUsername(System.getProperty("amqp.username"));
        factory.setPassword(System.getProperty("amqp.password"));
        factory.setVirtualHost("/");
        factory.setHost("localhost");
        factory.setPort(5672);
    }
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Asynchronous
    public void listen(
            @Observes(during = TransactionPhase.AFTER_COMPLETION) 
            final ValidationRequest request
    ) {
        try (Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()) {
            
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT, true);
            channel.queueDeclare(EXCHANGE_NAME, true, false, false, null).getQueue();
            channel.queueBind(EXCHANGE_NAME, EXCHANGE_NAME, ROUTING_KEY);

            final String message = "{"
                    + "\"contactId\": " + request.getContactId() + ", "
                    + "\"addressId\": " + request.getPosition() + ", "
                    + "\"verified\": true"
                    + "}";

            LOG.info("Sending JMS message");
            channel.basicPublish(EXCHANGE_NAME, ROUTING_KEY, null, message.getBytes());
        } catch (final Throwable ex) {
            LOG.severe(ex.getMessage());
        }
    }
    
}
