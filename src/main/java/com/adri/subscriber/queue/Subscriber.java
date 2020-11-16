package com.adri.subscriber.queue;

import com.solacesystems.jcsmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class Subscriber {

    private static Logger logger = LoggerFactory.getLogger(Subscriber.class);

    public void retrieveMsg(String hostname, String username, String vpn, String password, String queueName) throws JCSMPException {
        logger.info("TopicSubscriber initializing...");
        final JCSMPProperties properties = new JCSMPProperties();
        properties.setProperty(JCSMPProperties.HOST, hostname);     // host:port
        properties.setProperty(JCSMPProperties.USERNAME, username); // client-username
        properties.setProperty(JCSMPProperties.VPN_NAME,  vpn); // message-vpn
        properties.setProperty(JCSMPProperties.PASSWORD, password); // password
        final Queue queue = JCSMPFactory.onlyInstance().createQueue(queueName);
        final JCSMPSession session = JCSMPFactory.onlyInstance().createSession(properties);

        session.connect();

        final CountDownLatch latch = new CountDownLatch(1); // used for synchronizing b/w threads

        System.out.printf("Attempting to bind to the queue '%s' on the appliance.%n", queueName);

        // Create a Flow be able to bind to and consume messages from the Queue.
        final ConsumerFlowProperties flow_prop = new ConsumerFlowProperties();
        flow_prop.setEndpoint(queue);
        flow_prop.setAckMode(JCSMPProperties.SUPPORTED_MESSAGE_ACK_CLIENT);

        EndpointProperties endpoint_props = new EndpointProperties();
        endpoint_props.setAccessType(EndpointProperties.ACCESSTYPE_NONEXCLUSIVE);

        final FlowReceiver cons = session.createFlow(new XMLMessageListener() {
            @Override
            public void onReceive(BytesXMLMessage msg) {
                if (msg instanceof TextMessage) {
                    logger.info("TextMessage received: '%s'%n", ((TextMessage) msg).getText());
                } else {
                    logger.info("Message received.");
                }
                logger.info("Message Dump:%n%s%n", msg.dump());

                // When the ack mode is set to SUPPORTED_MESSAGE_ACK_CLIENT,
                // guaranteed delivery messages are acknowledged after
                // processing
                msg.ackMessage();
                latch.countDown(); // unblock main thread
            }

            @Override
            public void onException(JCSMPException e) {
                logger.info("Consumer received exception: %s%n", e);
                latch.countDown(); // unblock main thread
            }
        }, flow_prop, endpoint_props);

        // Start the consumer
        logger.info("Connected. Awaiting message ...");
        cons.start();

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
