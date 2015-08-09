package com.pkrete.xrd4j.tools.test_client.request;

import com.pkrete.xrd4j.client.serializer.ServiceRequestSerializer;
import com.pkrete.xrd4j.common.exception.XRd4JException;
import com.pkrete.xrd4j.common.member.ConsumerMember;
import com.pkrete.xrd4j.common.member.ProducerMember;
import com.pkrete.xrd4j.common.message.ServiceRequest;
import com.pkrete.xrd4j.common.util.MessageHelper;
import com.pkrete.xrd4j.tools.test_client.serializer.HelloServiceRequestSerializer;
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

    public static ServiceRequest getRequest(Properties clients, ServiceRequestSerializer serializer) {
        String clientStr = (String) clients.get("client");
        String[] clientArr = clientStr.split("\\|");
        String serviceStr = (String) clients.get("service");
        String[] serviceArr = serviceStr.split("\\|");

        try {
            String instance = clientArr[0];
            String memberClass = clientArr[1];
            String memberCode = clientArr[2];
            String subsystem = clientArr[3];
            String data = ApplicationHelper.getRandomString(Integer.parseInt(clientArr[4]));

            ConsumerMember consumer = null;
            if (subsystem.isEmpty()) {
                consumer = new ConsumerMember(instance, memberClass, memberCode);
            } else {
                consumer = new ConsumerMember(instance, memberClass, memberCode, subsystem);
            }

            instance = serviceArr[0];
            memberClass = serviceArr[1];
            memberCode = serviceArr[2];
            subsystem = serviceArr[3];
            String serviceCode = serviceArr[4];
            String serviceVersion = serviceArr[5];

            ProducerMember producer = new ProducerMember(instance, memberClass, memberCode, subsystem, serviceCode, serviceVersion);
            producer.setNamespaceUrl(serviceArr[6]);
            producer.setNamespacePrefix("ns");

            ServiceRequest<String> request = new ServiceRequest<String>(consumer, producer, MessageHelper.generateId());
            request.setUserId("tester");
            request.setRequestData(data);

            if (!clientArr[5].equals("0")) {
                String attachmentData = ApplicationHelper.getRandomString(Integer.parseInt(clientArr[5]));
                ((HelloServiceRequestSerializer) serializer).setAttachment("<attachmentData>" + attachmentData + "</attachmentData>");
                ((HelloServiceRequestSerializer) serializer).setAttachmentContentType("text/xml");
            }
            return request;
        } catch (XRd4JException e) {
        }
        return null;
    }
}
