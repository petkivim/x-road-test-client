package com.pkrete.xroadtestclient.request.thread;

import org.niis.xrd4j.client.SOAPClient;
import org.niis.xrd4j.client.SOAPClientImpl;
import org.niis.xrd4j.client.deserializer.ServiceResponseDeserializer;
import org.niis.xrd4j.client.serializer.ServiceRequestSerializer;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.message.ServiceResponse;
import org.niis.xrd4j.common.util.MessageHelper;

import com.pkrete.xroadtestclient.deserializer.TestServiceResponseDeserializer;
import com.pkrete.xroadtestclient.log.TestClientLogger;
import com.pkrete.xroadtestclient.log.TestClientLoggerImpl;
import com.pkrete.xroadtestclient.request.TestServiceRequest;
import com.pkrete.xroadtestclient.util.ApplicationHelper;
import com.pkrete.xroadtestclient.util.StatisticsCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.soap.SOAPMessage;

/**
 * @author Petteri Kivimäki
 */
public class Worker implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(Runnable.class);
    private final ServiceRequest message;
    private final String url;
    private final int sleep;
    private final int maxRequestCount;
    private final int maxTime;
    private final int number;
    private final ServiceRequestSerializer serializer;
    private final TestClientLogger resulstLogger;

    /**
     * Constructs and initializes a new Worker object.
     * @param message ServiceRequest to be sent
     * @param url target URL
     * @param sleep how many ms the Worker should sleep between two requests
     * @param maxRequestCount maximum number of requests to be sent
     * @param maxTime maximum time for this Worker to run
     * @param number number identifying this Worker
     * @param serializer ServiceRequestSerializer that is knows how to serialize the message
     */
    public Worker(ServiceRequest message, String url, int sleep, int maxRequestCount, int maxTime, int number, ServiceRequestSerializer serializer) {
        this.message = message;
        this.url = url;
        this.sleep = sleep;
        this.maxRequestCount = maxRequestCount;
        this.maxTime = maxTime;
        this.number = number;
        this.serializer = serializer;
        this.resulstLogger = new TestClientLoggerImpl();
    }

    @Override
    public void run() {
        LOG.debug("Thread #{} starting.", this.number);
        // Init variables for counting requests and time
        int requestCount = 0;
        long timeCount = 0;
        long startTime = System.currentTimeMillis();
        // Keep on sending messages until the conditions are met
        while (requestCount < this.maxRequestCount || timeCount < this.maxTime) {
            // Init variables for logging
            long throughput = 0;
            boolean sendSuccess = false;
            boolean receiveSuccess = true;
            String serviceProcessingTime = "0";
            // Get unique ID for the message
            String reqId = MessageHelper.generateId();
            try {
                // Set message ID
                message.setId(reqId);
                // Serialize message to SOAP
                SOAPMessage request = this.serializer.serialize(message);
                LOG.debug("Thread #{} sending message #{}, ID : \"{}\".", this.number, requestCount, reqId);
                long msgStartTime = System.currentTimeMillis();
                // Create new client for sending the message
                SOAPClient client = new SOAPClientImpl();
                // Send the message
                SOAPMessage soapResponse = client.send(request, url);
                sendSuccess = true;
                // Calculate message throughput time
                throughput = System.currentTimeMillis() - msgStartTime;
                // Deserialize the response
                ServiceResponseDeserializer deserializer = new TestServiceResponseDeserializer();
                ServiceResponse<TestServiceRequest, String> serviceResponse = deserializer.deserialize(soapResponse);
                // Check SOAP response for SOAP Fault
                if (serviceResponse.hasError()) {
                    receiveSuccess = false;
                    LOG.error("Thread #{} received response containing SOAP Fault for message #{}, ID : \"{}\".", this.number, requestCount, reqId);
                    LOG.error("Fault code : \"{}\".", serviceResponse.getErrorMessage().getFaultCode());
                } else {
                    serviceProcessingTime = serviceResponse.getResponseData();
                    LOG.debug("Thread #{} received response for message #{}, ID : \"{}\".", this.number, requestCount, reqId);
                }
                LOG.info("Message \"{}\" processing time {} ms", reqId, throughput);
                // Sleep...
                if (this.sleep > 0) {
                    LOG.debug("Thread #{} sleeping {} ms.", this.number, this.sleep);
                    Thread.sleep(this.sleep);
                }
            } catch (Exception ex) {
                receiveSuccess = false;
                LOG.error("Thread #{} sending message #{} failed, ID : \"{}\".", this.number, requestCount, reqId);
                LOG.error(ex.getMessage(), ex);
            }
            this.resulstLogger.log(this.number, reqId, throughput, serviceProcessingTime, sendSuccess, receiveSuccess);
            // Incerease time count
            timeCount = System.currentTimeMillis() - startTime;
            // Collect statistics
            long totalTime = throughput - ApplicationHelper.strToInt(serviceProcessingTime);
            StatisticsCollector.getStatisticsCollector().addResult(totalTime);
            StatisticsCollector.getStatisticsCollector().setMinThroughput(throughput);
            StatisticsCollector.getStatisticsCollector().setMaxThroughput(throughput);
            if (receiveSuccess) {
                StatisticsCollector.getStatisticsCollector().increseSuccessCount();
            } else {
                StatisticsCollector.getStatisticsCollector().increaseFailureCount();
            }
            // Update request counter
            requestCount++;
        }
        LOG.debug("Thread #{} done!", this.number);
    }
}
