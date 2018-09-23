package com.pkrete.xroadtestclient.request;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.member.ConsumerMember;
import org.niis.xrd4j.common.member.ProducerMember;
import org.niis.xrd4j.common.message.ServiceRequest;
import org.niis.xrd4j.common.util.ConfigurationHelper;
import org.niis.xrd4j.common.util.MessageHelper;

import com.pkrete.xroadtestclient.util.ApplicationHelper;
import com.pkrete.xroadtestclient.util.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * This class generates clients based on the configuration file.
 *
 * @author Petteri Kivimäki
 */
public final class RequestFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RequestFactory.class);

    /**
     * Constructs and initializes a new RequestFactory object. Should never be
     * used.
     */
    private RequestFactory() {

    }

    /**
     * Creates a new ServiceRequest object targeted to X-Road Test Service using the given
     * properties.
     * @param clients properties that define client, service and request size
     * @return new ServiceRequest object
     */
    public static ServiceRequest getRequest(Properties clients) {
        LOG.info("Starting to generate new request.");
        LOG.debug("Parse configuration.");
        String clientStr = (String) clients.get(Constants.CLIENT);
        String serviceStr = (String) clients.get(Constants.SERVICE);

        // Create client
        ConsumerMember consumer = ConfigurationHelper.parseConsumerMember(clientStr);
        if (consumer == null) {
            LOG.error("Invalid client configuration! Invalid value : \"{}\".", clientStr);
            return null;
        }
        LOG.debug("Client : \"{}\".", consumer);

        // Create service
        ProducerMember producer = ConfigurationHelper.parseProducerMember(serviceStr);
        if (producer == null) {
            LOG.error("Invalid client configuration! Invalid value : \"{}\".", serviceStr);
            return null;
        }
        LOG.debug("Service : \"{}\".", producer);

        try {
            LOG.debug("Configure client.");
            // Get configuration
            String requestBodySize = (String) clients.get(Constants.CLIENT_REQ_BODY_SIZE);
            String requestAttachmentSize = (String) clients.get(Constants.CLIENT_REQ_ATTACH_SIZE);
            String responseBodySize = (String) clients.get(Constants.CLIENT_RESPONSE_BODY_SIZE);
            String responseAttachmentSize = (String) clients.get(Constants.CLIENT_RESPONSE_ATTACH_SIZE);

            LOG.debug("Request body size : {}.", requestBodySize);
            LOG.debug("Request attachment size : {}.", requestAttachmentSize);
            LOG.debug("Response body size : {}.", responseBodySize);
            LOG.debug("Response attachment size : {}.", responseAttachmentSize);

            // Generate request data
            String data = ApplicationHelper.getRandomString(Integer.parseInt(requestBodySize));
            String attachmentData = ApplicationHelper.getRandomString(Integer.parseInt(requestAttachmentSize));

            LOG.debug("Configure service.");
            // Get configuration
            String namespace = (String) clients.get(Constants.SERVICE_NAMESPACE);
            // Set values
            producer.setNamespaceUrl(namespace);
            LOG.debug("Namespace : \"{}\".", namespace);
            producer.setNamespacePrefix("ns");
            LOG.debug("Namespace prefix : \"ns\".");

            LOG.debug("Generate request.");
            // Create a new service request which request data type is String
            ServiceRequest<TestServiceRequest> request = new ServiceRequest<>(consumer, producer, MessageHelper.generateId());
            // Set user id
            request.setUserId("tester");
            LOG.debug("User id : \"tester\".");
            // Creare new TestServiceRequest
            TestServiceRequest testServiceRequest = new TestServiceRequest(data, attachmentData, responseBodySize, responseAttachmentSize);
            // Set request data
            request.setRequestData(testServiceRequest);
            LOG.info("The request was succesfully generated.");
            return request;
        } catch (XRd4JException e) {
            LOG.error(e.getMessage(), e);
        }
        LOG.error("Generating the request failed. Null is returned.");
        return null;
    }
}
