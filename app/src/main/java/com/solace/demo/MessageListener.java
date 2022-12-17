package com.solace.demo;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.XMLMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListener implements XMLMessageListener {
    final Logger logger = LoggerFactory.getLogger(MessageListener.class);
    private App app;
    public MessageListener(App app) {
        this.app = app;
    }

    @Override
    public void onReceive(BytesXMLMessage msg) {
        app.onMessageReceived(msg);
    }

    @Override
    public void onException(JCSMPException e) {
        logger.error("Consumer received exception: {}", e.toString());
    }
}
