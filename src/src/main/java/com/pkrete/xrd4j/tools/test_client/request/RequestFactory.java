package com.pkrete.xrd4j.tools.test_client.request;

import com.pkrete.xrd4j.common.exception.XRd4JException;
import com.pkrete.xrd4j.common.member.ConsumerMember;
import com.pkrete.xrd4j.common.member.ProducerMember;
import com.pkrete.xrd4j.common.message.ServiceRequest;
import com.pkrete.xrd4j.common.util.MessageHelper;
import com.pkrete.xrd4j.tools.test_client.util.ApplicationHelper;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class generates clients based on the config file.
 *
 * @author Petteri Kivimäki
 */
public class RequestFactory {

    private static final Logger logger = LoggerFactory.getLogger(RequestFactory.class);

    public static ServiceRequest getRequest(Properties clients) {
        logger.info("Starting to generate new request.");
        logger.debug("Parse configuration.");
        String clientStr = (String) clients.get("client");
        String[] clientArr = clientStr.split("\\|");
        String serviceStr = (String) clients.get("service");
        String[] serviceArr = serviceStr.split("\\|");

        if (clientArr.length != 8) {
            logger.error("Invalid client configuration! The number of configuration items does not match ({} != 8).", clientArr.length);
            return null;
        }
        if (serviceArr.length != 7) {
            logger.error("Invalid service configuration! The number of configuration items does not match ({} != 7).", serviceArr.length);
            return null;
        }
        try {
            logger.debug("Configure client.");
            String instance = clientArr[0];
            String memberClass = clientArr[1];
            String memberCode = clientArr[2];
            String subsystem = clientArr[3];
            String data = ApplicationHelper.getRandomString(Integer.parseInt(clientArr[4]));
            String attachmentData = ApplicationHelper.getRandomString(Integer.parseInt(clientArr[5]));
            String responseBodySize = clientArr[6];
            String responseAttachmentSize = clientArr[7];

            logger.debug("Request body size : {}.", clientArr[4]);
            logger.debug("Request attachment size : {}.", clientArr[5]);
            logger.debug("Response body size : {}.", clientArr[6]);
            logger.debug("Response attachment size : {}.", clientArr[7]);

            ConsumerMember consumer = null;
            if (subsystem.isEmpty()) {
                consumer = new ConsumerMember(instance, memberClass, memberCode);
            } else {
                consumer = new ConsumerMember(instance, memberClass, memberCode, subsystem);
            }

            logger.debug("Client : \"{}\".", consumer.toString());

            logger.debug("Configure service.");
            instance = serviceArr[0];
            memberClass = serviceArr[1];
            memberCode = serviceArr[2];
            subsystem = serviceArr[3];
            String serviceCode = serviceArr[4];
            String serviceVersion = serviceArr[5];

            ProducerMember producer = new ProducerMember(instance, memberClass, memberCode, subsystem, serviceCode, serviceVersion);
            logger.debug("Service : \"{}\".", producer.toString());
            producer.setNamespaceUrl(serviceArr[6]);
            producer.setNamespacePrefix("ns");

            logger.debug("Generate request.");
            // Create a new service request which request data type is String
            ServiceRequest<TestServiceRequest> request = new ServiceRequest<TestServiceRequest>(consumer, producer, MessageHelper.generateId());
            // Set user id
            request.setUserId("tester");
            // Creare new TestServiceRequest
            TestServiceRequest testServiceRequest = new TestServiceRequest(data, attachmentData, responseBodySize, responseAttachmentSize);
            // Set request data
            request.setRequestData(testServiceRequest);
            logger.info("The request was succesfully generated.");
            return request;
        } catch (XRd4JException e) {
        }
        logger.error("Generating the request failed. Null is returned.");
        return null;
    }
}
