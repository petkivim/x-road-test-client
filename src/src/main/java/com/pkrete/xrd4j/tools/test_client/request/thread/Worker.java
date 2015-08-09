package com.pkrete.xrd4j.tools.test_client.request.thread;

import com.pkrete.xrd4j.client.SOAPClient;
import com.pkrete.xrd4j.client.SOAPClientImpl;
import com.pkrete.xrd4j.client.serializer.ServiceRequestSerializer;
import com.pkrete.xrd4j.common.message.ServiceRequest;
import com.pkrete.xrd4j.common.util.MessageHelper;
import com.pkrete.xrd4j.tools.test_client.log.TestClientLogger;
import com.pkrete.xrd4j.tools.test_client.log.TestClientLoggerImpl;
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
        int requestCount = 0;
        long timeCount = 0;
        long startTime = System.currentTimeMillis();
        while (requestCount < this.maxRequestCount || timeCount < this.maxTime) {
            long throughput = 0;
            boolean sendSuccess = false;
            boolean receiveSuccess = true;
            String reqId = MessageHelper.generateId();
            try {
                message.setId(reqId);
                SOAPMessage request = this.serializer.serialize(message);
                logger.debug("Thread #{} sending message #{}, ID : \"{}\".", this.number, requestCount, reqId);
                long msgStartTime = System.currentTimeMillis();
                SOAPClient client = new SOAPClientImpl();
                client.send(request, url);
                sendSuccess = true;
                throughput = System.currentTimeMillis() - msgStartTime;
                logger.debug("Thread #{} received response for message #{}, ID : \"{}\".", this.number, requestCount, reqId);
                logger.info("Message \"{}\" processing time {} ms", reqId, throughput);
                if (this.sleep > 0) {
                    logger.debug("Thread #{} sleeping {} ms.", this.number, this.sleep);
                    Thread.sleep(this.sleep);
                }
            } catch (Exception ex) {
                receiveSuccess = false;
                logger.error("Thread #{} sending message #{} failed, ID : \"{}\".", this.number, requestCount, reqId);
                logger.error(ex.getMessage());
            }
            this.resulstLogger.log(this.number, reqId, throughput, sendSuccess, receiveSuccess);
            timeCount = System.currentTimeMillis() - startTime;
            requestCount++;
        }
        logger.debug("Thread #{} done!", this.number);
    }
}
