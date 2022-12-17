package com.solace.demo;

import com.solace.demo.geofiltering.FilteringRequest;
import com.solacesystems.jcsmp.BytesXMLMessage;
import com.solacesystems.jcsmp.JCSMPException;
import com.solacesystems.jcsmp.XMLMessageListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageListener implements XMLMessageListener {
    final Logger logger = LoggerFactory.getLogger(MessageListener.class);
    @Override
    public void onReceive(BytesXMLMessage msg) {
        if (msg.hasAttachment()) {
            try {
                var request = FilteringRequest.from(msg.getAttachmentByteBuffer().array());
                logger.debug("Received Request: \n{}", request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            logger.info("Received Message: type->{}, AttachmentContentLength->{}, ContentLength()->{}",
                    msg.getClass().getCanonicalName(),
                    msg.getAttachmentContentLength(),
                    msg.getContentLength());
        }
    }

    @Override
    public void onException(JCSMPException e) {
        logger.error("Consumer received exception: {}", e.toString());
    }
}
