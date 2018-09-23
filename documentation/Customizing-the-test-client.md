## Customizing the Test Client

This document describes how Test Client can be used for calling other services than [X-Road Test Service](https://github.com/nordic-institute/X-Road-test-service). The implementation of Test Client is based on [XRd4J](https://github.com/nordic-institute/xrd4j) library.

The default implementation calls X-Road Test Service's ```testService``` service using a random String, which size can be configured, as a parameter. The String can be placed in the message body and/or message attachment, and the same String is used in all the requests in a single execution. ```testService``` takes as parameters the size of the response body and the size of the response attachment part. The size defines the number of characters in the response. Calling some other service requires changes in the following classes:

* ```com.pkrete.xroadtestclient.request.RequestFactory``` : class that generates clients based on the config file
* ```com.pkrete.xroadtestclient.serializer.TestServiceRequestSerializer``` : class that serializes SOAP requests

#### RequestFactory

The default implementation's lines 72-80 look like this:

```
// Create a new service request which request data type is String
ServiceRequest<TestServiceRequest> request = new ServiceRequest<TestServiceRequest>(consumer, producer, MessageHelper.generateId());
// Set user id
request.setUserId("tester");
logger.debug("User id : \"tester\".");
// Create new TestServiceRequest
TestServiceRequest testServiceRequest = new TestServiceRequest(requestBody, requestAttachment, responseBodySize, responseAttachmentSize);
// Set request data
request.setRequestData(testServiceRequest);
```

```TestServiceRequest``` holds all the request parameters (request body, request attachment, response body size, response attachment size) that are included in the request.


Let's take a look at a ```Person``` class with three instance variables:

```
public class Person {
  private String firstName;
  private String lastName;
  private String SSN;

  public String toString() {
    return "{\"firstName\":\"" + this.firstName + "\", \"lastName\":\"" + this.lastName + "\", \"SSN\":\"" + this.SSN + "\"}";
  }
}
```

With ```Person``` class ```RequestFactory```'s lines 72-80 would look like this:

```
// Create a new service request which request data type is Person
ServiceRequest<Person> request = new ServiceRequest<Person>(consumer, producer, MessageHelper.generateId());
request.setUserId("tester");
// Initialize new Person object
Person person = new Person(...);
// Set person variable as request data
request.setRequestData(person);
```

#### TestServiceRequestSerializer

Request serializer is always class specific which means that each class that is used as a request data must have its own request serializer implementation. The default implementation of ```serializeRequest``` method looks like this:

```
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
```

The above code generates the XML below (no attachment in this example):

```
<responseBodySize>...</responseBodySize>
<responseAttachmentSize>...</responseAttachmentSize>
<payload>...</payload>
```

The values can be accessed by calling e.g. ```((TestServiceRequest) request.getRequestData()).getResponseBodySize()```.

Let's take a look at the Person class. The XML presentation of the Person class could look like this:

```
<person>
  <firstName>...</firstName>
  <lastName>...</lastName>
  <SSN>...</SSN>
</person>
```

Person class needs it own request serializer implementation. The implementation of ```serializeRequest``` that generates the above XML looks like this:

```
protected void serializeRequest(ServiceRequest request, SOAPElement soapRequest, SOAPEnvelope envelope) throws SOAPException {
  // Create "Person" element
  SOAPElement person = soapRequest.addChildElement(envelope.createName("Person"));

  // Create "firstName" child element under "Person"
  SOAPElement firstName= person.addChildElement(envelope.createName("firstName"));
  // Add value to "firstName"
  firstName.addTextNode(( (Person) request.getRequestData() ).getFirstName());

  // Create "lastName" child element under "Person"
  SOAPElement lastName= person.addChildElement(envelope.createName("lastName"));
  // Add value to "firstName"
  lastName.addTextNode(( (Person) request.getRequestData() ).getLastName());

  // Create "SSN" child element under "Person"
  SOAPElement ssn = person.addChildElement(envelope.createName("SSN"));
  // Add value to "firstName"
  ssn.addTextNode(( (Person) request.getRequestData() ).getSSN());

  if (attachment != null && attachmentContentType != null) {
    AttachmentPart attachPart = request.getSoapMessage().createAttachmentPart(attachment, attachmentContentType);
    attachPart.setContentId("attachment_id");
    request.getSoapMessage().addAttachmentPart(attachPart);
  }
```

The ```serializeRequest``` method implements the serialization of the request data from Java object to SOAP. [XRd4J](https://github.com/nordic-institute/xrd4j) library takes care of adding mandatory X-Road SOAP headers and other elements around the payload and the SOAP request generated by the Test Client looks like this:

```
<SOAP-ENV:Envelope xmlns:SOAP-ENV="http://schemas.xmlsoap.org/soap/envelope/" xmlns:id="http://x-road.eu/xsd/identifiers" xmlns:xrd="http://x-road.eu/xsd/xroad.xsd">
    <SOAP-ENV:Header>
        <xrd:client id:objectType="SUBSYSTEM">
            <id:xRoadInstance>NIIS-TEST</id:xRoadInstance>
            <id:memberClass>GOV</id:memberClass>
            <id:memberCode>123456-7</id:memberCode>
            <id:subsystemCode>TestClient</id:subsystemCode>
        </xrd:client>
        <xrd:service id:objectType="SERVICE">
            <id:xRoadInstance>NIIS-TEST</id:xRoadInstance>
            <id:memberClass>GOV</id:memberClass>
            <id:memberCode>0245437-2</id:memberCode>
            <id:subsystemCode>TestService</id:subsystemCode>
            <id:serviceCode>personService</id:serviceCode>
            <id:serviceVersion>v1</id:serviceVersion>
        </xrd:service>
        <xrd:userId>test</xrd:userId>
        <xrd:id>ID11234</xrd:id>
        <xrd:protocolVersion>4.0</xrd:protocolVersion>
    </SOAP-ENV:Header>
    <SOAP-ENV:Body>
        <ns:personService xmlns:ns1="http://test.x-road.global/producer">
            <ns:request>
                <ns:person>
                    <ns:firstName>...</ns:firstName>
                    <ns:lastName>...</ns:lastName>
                    <ns:SSN>...</ns:SSN>
                </ns:person>
            </ns:request>
        </ns:personService>
    </SOAP-ENV:Body>
</SOAP-ENV:Envelope> 
```

In addition to the code changes ```clients.properties``` file must be configured accordingly. **N.B.** After the modifications message body size and message attachment body size properties can be set to 0 as they don't have any effect. However, they **must not be removed**. 