package com.pkrete.xrd4j.tools.test_client.log;

/**
 * This class defines an interface for logging the data related to individual
 * messages.
 *
 * @author Petteri Kivimäki
 */
public interface TestClientLogger {

    void log(int threadId, String msgId, long throughput, boolean successSend, boolean successReceive);
}
