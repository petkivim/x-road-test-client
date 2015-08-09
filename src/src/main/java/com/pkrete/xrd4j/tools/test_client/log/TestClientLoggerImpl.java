package com.pkrete.xrd4j.tools.test_client.log;

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
     * @param successSend true if the message was sent succesfully; otherwise
     * false
     * @param successReceive true if the response was received succesfully;
     * otherwise false
     */
    public void log(int threadId, String msgId, long throughput, boolean successSend, boolean successReceive) {
        logger.info("{}\t{}\t{}\t{}\t{}", threadId, msgId, throughput, successSend, successReceive);
    }
}
