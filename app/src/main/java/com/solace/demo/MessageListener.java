package com.solace.demo;

import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.XMLMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListener implements XMLMessageListener {
    final Logger logger = LoggerFactory.getLogger(MessageListener.class);
    @Override
    public void onReceive(BytesXMLMessage bytesXMLMessage) {
        logger.info("Received Message: type->{}, AttachmentContentLength->{}, ContentLength()->{}",
                bytesXMLMessage.getClass().getCanonicalName(),
                bytesXMLMessage.getAttachmentContentLength(),
                bytesXMLMessage.getContentLength());
    }

    @Override
    public void onException(JCSMPException e) {
        logger.error("Consumer received exception: {}", e.toString());
    }
}
