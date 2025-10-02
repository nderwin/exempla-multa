/*
 * Copyright 2025 Nathan Erwin
 */
package com.github.nderwin.telemetry.control;

import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.Asynchronous;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

@Stateless
@LocalBean
public class ValidationRequestListener {

    private static final Logger LOG = Logger.getLogger(ValidationRequestListener.class.getName());
    
    @Resource(lookup = "java:/jms/activemq/topic/VirtualTopic.address-verification")
    Topic topic;
    
    @Resource(lookup = "java:/ActiveMQConnectionFactory")
    ConnectionFactory factory;
    
    @TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
    @Asynchronous
    public void listen(
            @Observes(during = TransactionPhase.AFTER_COMPLETION) 
            final ValidationRequest request
    ) {
        Connection connection = null;
        Session session = null;
        MessageProducer producer = null;
        
        try {
            connection = factory.createConnection();
            session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
            
            producer = session.createProducer(topic);
            producer.setDeliveryMode(DeliveryMode.PERSISTENT);
            
            final TextMessage message = session.createTextMessage("{"
                    + "\"contactId\": " + request.getContactId() + ", "
                    + "\"addressId\": " + request.getPosition() + ", "
                    + "\"verified\": true"
                    + "}");
            
            producer.send(message);
        } catch (final JMSException ex) {
            LOG.severe(ex.getMessage());
        } finally {
            if (null != producer) {
                try {
                    producer.close();
                } catch (final JMSException ex) {
                }
            }
            
            if (null != session) {
                try {
                    session.close();
                } catch (final JMSException ex) {
                }
            }
            
            if (null != connection) {
                try {
                    connection.close();
                } catch (final JMSException ex) {
                }
            }
        }
    }
    
}
