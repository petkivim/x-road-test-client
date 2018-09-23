package com.pkrete.xroadtestclient.request;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ProducerMember;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.util.ConfigurationHelper;
import org.niis.xrd4j.common.util.MessageHelper;
import com.pkrete.xroadtestclient.util.ApplicationHelper;
import com.pkrete.xroadtestclient.util.Constants;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class generates clients based on the configuration file.
 *
 * @author Petteri Kivimäki
 */
public class RequestFactory {

    private static final Logger logger = LoggerFactory.getLogger(RequestFactory.class);

    /**
     * Constructs and initializes a new RequestFactory object. Should never be
     * used.
     */
    private RequestFactory() {

    }

    public static ServiceRequest getRequest(Properties clients) {
        logger.info("Starting to generate new request.");
        logger.debug("Parse configuration.");
        String clientStr = (String) clients.get(Constants.CLIENT);
        String serviceStr = (String) clients.get(Constants.SERVICE);

        // Create client
        ConsumerMember consumer = ConfigurationHelper.parseConsumerMember(clientStr);
        if (consumer == null) {
            logger.error("Invalid client configuration! Invalid value : \"{}\".", clientStr);
            return null;
        }
        logger.debug("Client : \"{}\".", consumer.toString());

        // Create service
        ProducerMember producer = ConfigurationHelper.parseProducerMember(serviceStr);
        if (producer == null) {
            logger.error("Invalid client configuration! Invalid value : \"{}\".", serviceStr);
            return null;
        }
        logger.debug("Service : \"{}\".", producer.toString());

        try {
            logger.debug("Configure client.");
            // Get configuration
            String requestBodySize = (String) clients.get(Constants.CLIENT_REQ_BODY_SIZE);
            String requestAttachmentSize = (String) clients.get(Constants.CLIENT_REQ_ATTACH_SIZE);
            String responseBodySize = (String) clients.get(Constants.CLIENT_RESPONSE_BODY_SIZE);
            String responseAttachmentSize = (String) clients.get(Constants.CLIENT_RESPONSE_ATTACH_SIZE);

            logger.debug("Request body size : {}.", requestBodySize);
            logger.debug("Request attachment size : {}.", requestAttachmentSize);
            logger.debug("Response body size : {}.", responseBodySize);
            logger.debug("Response attachment size : {}.", responseAttachmentSize);

            // Generate request data
            String data = ApplicationHelper.getRandomString(Integer.parseInt(requestBodySize));
            String attachmentData = ApplicationHelper.getRandomString(Integer.parseInt(requestAttachmentSize));

            logger.debug("Configure service.");
            // Get configuration
            String namespace = (String) clients.get(Constants.SERVICE_NAMESPACE);
            // Set values
            producer.setNamespaceUrl(namespace);
            logger.debug("Namespace : \"{}\".", namespace);
            producer.setNamespacePrefix("ns");
            logger.debug("Namespace prefix : \"ns\".");

            logger.debug("Generate request.");
            // Create a new service request which request data type is String
            ServiceRequest<TestServiceRequest> request = new ServiceRequest<>(consumer, producer, MessageHelper.generateId());
            // Set user id
            request.setUserId("tester");
            logger.debug("User id : \"tester\".");
            // Creare new TestServiceRequest
            TestServiceRequest testServiceRequest = new TestServiceRequest(data, attachmentData, responseBodySize, responseAttachmentSize);
            // Set request data
            request.setRequestData(testServiceRequest);
            logger.info("The request was succesfully generated.");
            return request;
        } catch (XRd4JException e) {
            logger.error(e.getMessage(), e);
        }
        logger.error("Generating the request failed. Null is returned.");
        return null;
    }
}
