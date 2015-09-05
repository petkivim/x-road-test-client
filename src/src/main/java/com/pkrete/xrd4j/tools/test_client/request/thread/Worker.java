package com.pkrete.xrd4j.tools.test_client.request.thread;

import com.pkrete.xrd4j.client.SOAPClient;
import com.pkrete.xrd4j.client.SOAPClientImpl;
import com.pkrete.xrd4j.client.deserializer.ServiceResponseDeserializer;
import com.pkrete.xrd4j.client.serializer.ServiceRequestSerializer;
import com.pkrete.xrd4j.common.message.ServiceRequest;
import com.pkrete.xrd4j.common.message.ServiceResponse;
import com.pkrete.xrd4j.common.util.MessageHelper;
import com.pkrete.xrd4j.tools.test_client.deserializer.TestServiceResponseDeserializer;
import com.pkrete.xrd4j.tools.test_client.log.TestClientLogger;
import com.pkrete.xrd4j.tools.test_client.log.TestClientLoggerImpl;
import com.pkrete.xrd4j.tools.test_client.request.TestServiceRequest;
import javax.xml.soap.SOAPMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Petteri Kivimäki
 */
public class Worker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Runnable.class);
    private ServiceRequest message;
    private String url;
    private int sleep;
    private int maxRequestCount;
    private int maxTime;
    private int number;
    private ServiceRequestSerializer serializer;
    private TestClientLogger resulstLogger;

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
        logger.debug("Thread #{} starting.", this.number);
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
                logger.debug("Thread #{} sending message #{}, ID : \"{}\".", this.number, requestCount, reqId);
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
                    logger.error("Thread #{} received response containing SOAP Fault for message #{}, ID : \"{}\".", this.number, requestCount, reqId);
                    logger.error("Fault code : \"{}\".", serviceResponse.getErrorMessage().getFaultCode());
                } else {
                    serviceProcessingTime = serviceResponse.getResponseData();
                    logger.debug("Thread #{} received response for message #{}, ID : \"{}\".", this.number, requestCount, reqId);
                }
                logger.info("Message \"{}\" processing time {} ms", reqId, throughput);
                // Sleep...
                if (this.sleep > 0) {
                    logger.debug("Thread #{} sleeping {} ms.", this.number, this.sleep);
                    Thread.sleep(this.sleep);
                }
            } catch (Exception ex) {
                receiveSuccess = false;
                logger.error("Thread #{} sending message #{} failed, ID : \"{}\".", this.number, requestCount, reqId);
                logger.error(ex.getMessage());
            }
            this.resulstLogger.log(this.number, reqId, throughput, serviceProcessingTime, sendSuccess, receiveSuccess);
            timeCount = System.currentTimeMillis() - startTime;
            requestCount++;
        }
        logger.debug("Thread #{} done!", this.number);
    }
}
