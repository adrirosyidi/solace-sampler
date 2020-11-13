package com.adri.publisher;

import com.solacesystems.jcsmp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Publisher {

    private static Logger logger = LoggerFactory.getLogger(Publisher.class);

    public void sendMsg(String hostname, String username, String vpn) throws JCSMPException {
    final JCSMPProperties properties=new JCSMPProperties();
    properties.setProperty(JCSMPProperties.HOST,hostname);     // host:port
    properties.setProperty(JCSMPProperties.USERNAME,username); // client-username
    properties.setProperty(JCSMPProperties.VPN_NAME,vpn); // message-vpn
    final JCSMPSession session= JCSMPFactory.onlyInstance().createSession(properties);
    session.connect();

    final Topic topic=JCSMPFactory.onlyInstance().createTopic("tutorial/topic");

    /** Anonymous inner-class for handling publishing events */
    XMLMessageProducer prod=session.getMessageProducer(new JCSMPStreamingPublishEventHandler(){
    @Override
    public void responseReceived(String messageID){
            logger.info("Producer received response for msg: "+messageID);
            }
    @Override
    public void handleError(String messageID,JCSMPException e,long timestamp){
        logger.info("Producer received error for msg: %s@%s - %s%n",
        messageID,timestamp,e);
        }
    });
        // Publish-only session is now hooked up and running!
        TextMessage msg=JCSMPFactory.onlyInstance().createMessage(TextMessage.class);
    final String text="Hello world!";
        msg.setText(text);
        logger.info("Connected. About to send message "+ text +" to topic " + topic.getName());
        prod.send(msg,topic);
        logger.info("Message sent. Exiting.");
        session.closeSession();
    }
}
