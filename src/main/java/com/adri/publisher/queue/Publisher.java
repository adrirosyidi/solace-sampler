package com.adri.publisher.queue;

import com.solacesystems.jcsmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.Date;

public class Publisher {

    private static Logger logger = LoggerFactory.getLogger(Publisher.class);

    public void sendMsg(String hostname, String username, String vpn, String password, String queueName) throws JCSMPException {
    final JCSMPProperties properties=new JCSMPProperties();
    properties.setProperty(JCSMPProperties.HOST,hostname);     // host:port
    properties.setProperty(JCSMPProperties.USERNAME,username); // client-username
    properties.setProperty(JCSMPProperties.VPN_NAME,vpn); // message-vpn
        properties.setProperty(JCSMPProperties.PASSWORD, password); //password
    final JCSMPSession session= JCSMPFactory.onlyInstance().createSession(properties);
    session.connect();

        final Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);

        /** Anonymous inner-class for handling publishing events */
        final XMLMessageProducer prod = session.getMessageProducer(
                new JCSMPStreamingPublishEventHandler() {
                    @Override
                    public void responseReceived(String messageID) {
                        logger.info("Producer received response for msg ID #%s%n",messageID);
                    }
                    @Override
                    public void handleError(String messageID, JCSMPException e, long timestamp) {
                        logger.info("Producer received error for msg ID %s @ %s - %s%n",
                                messageID,timestamp,e);
                    }
                });

        // Publish-only session is now hooked up and running!
        logger.info("Connected. About to send message to queue " + queue.getName() + "\n");

        TextMessage msg = JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
        msg.setDeliveryMode(DeliveryMode.PERSISTENT);
        String text = "Hello POC ";
        msg.setText(text);

        // Send message directly to the queue
        prod.send(msg, queue);
        // Delivery not yet confirmed. See ConfirmedPublish.java
        logger.info("Message sent. Exiting.");

        // Close session
        session.closeSession();
    }
}
