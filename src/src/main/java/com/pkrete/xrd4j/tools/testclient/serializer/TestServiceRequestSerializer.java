package com.pkrete.xrd4j.tools.testclient.serializer;

import org.niis.xrd4j.client.serializer.AbstractServiceRequestSerializer;
import org.niis.xrd4j.common.message.ServiceRequest;
import com.pkrete.xrd4j.tools.testclient.request.TestServiceRequest;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;

/**
 * Class that serializes TestService requests.
 *
 * @author Petteri Kivimäki
 */
public class TestServiceRequestSerializer extends AbstractServiceRequestSerializer {

    @Override
    protected void serializeRequest(ServiceRequest request, SOAPElement soapRequest, SOAPEnvelope envelope) throws SOAPException {
        // Get TestServiceRequest object
        TestServiceRequest testServiceRequest = (TestServiceRequest) request.getRequestData();
        // Add responseBodySize element
        SOAPElement responseBodySize = soapRequest.addChildElement(envelope.createName("responseBodySize"));
        responseBodySize.addTextNode(testServiceRequest.getResponseBodySize());
        // Add responseAttachmentSize element
        SOAPElement responseAttachmentSize = soapRequest.addChildElement(envelope.createName("responseAttachmentSize"));
        responseAttachmentSize.addTextNode(testServiceRequest.getResponseAttachmentSize());
        // Add request payload
        SOAPElement payload = soapRequest.addChildElement(envelope.createName("payload"));
        payload.addTextNode(testServiceRequest.getRequestPayload());
        // Add request attachment
        if (testServiceRequest.getRequestAttachment() != null && !testServiceRequest.getRequestAttachment().isEmpty()) {
            String attachment = "<attachmentData>" + testServiceRequest.getRequestAttachment() + "</attachmentData>";
            AttachmentPart attachPart = request.getSoapMessage().createAttachmentPart(attachment, "text/xml");
            attachPart.setContentId("attachment_id");
            request.getSoapMessage().addAttachmentPart(attachPart);
        }
    }
}
