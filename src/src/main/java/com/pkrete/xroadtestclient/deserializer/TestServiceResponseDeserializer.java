package com.pkrete.xroadtestclient.deserializer;

import org.niis.xrd4j.client.deserializer.AbstractResponseDeserializer;
import com.pkrete.xroadtestclient.request.TestServiceRequest;
import javax.xml.soap.Node;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

/**
 * This class deserializes the response returned by the Test Service. The only
 * interesting element in the response is the "processingTime" element. All the
 * other elements are ignored.
 * 
 * @author Petteri Kivimäki
 */
public class TestServiceResponseDeserializer extends AbstractResponseDeserializer<TestServiceRequest, String> {

    @Override
    protected TestServiceRequest deserializeRequestData(Node requestNode) throws SOAPException {
        return null;
    }

    @Override
    protected String deserializeResponseData(Node responseNode, SOAPMessage message) throws SOAPException {
        // Look for the "processingTime" node, ignore all the other elements
        for (int i = 0; i < responseNode.getChildNodes().getLength(); i++) {
            if (responseNode.getChildNodes().item(i).getNodeType() == Node.ELEMENT_NODE
                    && "processingTime".equals(responseNode.getChildNodes().item(i).getLocalName())) {
                return responseNode.getChildNodes().item(i).getTextContent();
            }
        }
        return null;
    }
}
