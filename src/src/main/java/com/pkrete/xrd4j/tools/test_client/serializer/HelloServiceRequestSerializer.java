package com.pkrete.xrd4j.tools.test_client.serializer;

import com.pkrete.xrd4j.client.serializer.AbstractServiceRequestSerializer;
import com.pkrete.xrd4j.common.message.ServiceRequest;
import javax.xml.soap.AttachmentPart;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;

/**
 * Class that serializes HelloService requests.
 *
 * @author Petteri Kivimäki
 */
public class HelloServiceRequestSerializer extends AbstractServiceRequestSerializer {

    private String attachment;
    private String attachmentContentType;

    @Override
    protected void serializeRequest(ServiceRequest request, SOAPElement soapRequest, SOAPEnvelope envelope) throws SOAPException {
        SOAPElement data = soapRequest.addChildElement(envelope.createName("name"));
        data.addTextNode((String) request.getRequestData());

        if (attachment != null && attachmentContentType != null) {
            AttachmentPart attachPart = request.getSoapMessage().createAttachmentPart(attachment, attachmentContentType);
            attachPart.setContentId("attachment_id");
            request.getSoapMessage().addAttachmentPart(attachPart);
        }
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getAttachmentContentType() {
        return attachmentContentType;
    }

    public void setAttachmentContentType(String attachmentContentType) {
        this.attachmentContentType = attachmentContentType;
    }
}