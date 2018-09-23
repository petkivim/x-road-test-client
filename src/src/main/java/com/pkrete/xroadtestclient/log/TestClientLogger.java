package com.pkrete.xroadtestclient.log;

/**
 * This class defines an interface for logging the data related to individual
 * messages.
 *
 * @author Petteri Kivimäki
 */
public interface TestClientLogger {

    void log(int threadId, String msgId, long throughput, String serviceProcessingTime, boolean successSend, boolean successReceive);
}
