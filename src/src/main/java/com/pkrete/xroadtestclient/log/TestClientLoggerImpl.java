package com.pkrete.xroadtestclient.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsible of logging the data related to individual
 * messages.
 *
 * @author Petteri Kivimäki
 */
public class TestClientLoggerImpl implements TestClientLogger {

    private static final Logger logger = LoggerFactory.getLogger(TestClientLoggerImpl.class);

    /**
     * Write the given information into a log.
     * @param threadId id of the thread that processed the message
     * @param msgId unique id of the message
     * @param throughput message throughput in milliseconds
     * @param serviceProcessingTime time that the service used for generating the response data in milliseconds
     * @param successSend true if the message was sent successfully; otherwise
     * false
     * @param successReceive true if the response was received successfully;
     * otherwise false
     */
    public void log(int threadId, String msgId, long throughput, String serviceProcessingTime, boolean successSend, boolean successReceive) {
        logger.info("{}\t{}\t{}\t{}\t{}\t{}", threadId, msgId, throughput, serviceProcessingTime, successSend, successReceive);
    }
}
