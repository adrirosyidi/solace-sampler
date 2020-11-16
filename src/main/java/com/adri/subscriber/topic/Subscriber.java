package com.adri.subscriber.topic;

import com.solacesystems.jcsmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Subscriber {

    private static Logger logger = LoggerFactory.getLogger(Subscriber.class);

    public void retrieveMsg(String hostname, String username, String vpn) throws JCSMPException {
        logger.info("TopicSubscriber initializing...");
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, hostname);     // host:port
        properties.setProperty(JCSMPProperties.USERNAME, username); // client-username
        properties.setProperty(JCSMPProperties.VPN_NAME,  vpn); // message-vpn
        final Topic topic = JCSMPFactory.onlyInstance().createTopic("tutorial/topic");
        final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);

        session.connect();

        final CountDownLatch latch = new CountDownLatch(1); // used for synchronizing b/w threads
        /** Anonymous inner-class for MessageListener
         *  This demonstrates the async threaded message callback */
        final XMLMessageConsumer cons = session.getMessageConsumer(new XMLMessageListener() {
            @Override
            public void onReceive(BytesXMLMessage msg) {
                if (msg instanceof TextMessage) {
                    logger.info("TextMessage received: " + ((TextMessage)msg).getText());
                } else {
                    logger.info("Message received.");
                }
                logger.info("Message Dump: \n " + msg.dump());
                latch.countDown();  // unblock main thread
            }

            @Override
            public void onException(JCSMPException e) {
                logger.info("Consumer received exception: " + e);
                latch.countDown();  // unblock main thread
            }
        });
        session.addSubscription(topic);
        logger.info("Connected. Awaiting message...");
        cons.start();
        // Consume-only session is now hooked up and running!

        try {
            latch.await(5, TimeUnit.SECONDS); // block here until message received, and latch will flip
        } catch (InterruptedException e) {
            logger.info("I was awoken while waiting");
        }
        // Close consumer
        cons.close();
        logger.info("Exiting.");
        session.closeSession();
    }
}
