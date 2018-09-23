package com.pkrete.xroadtestclient.request;

/**
 * This class represents TestServiceRequest.
 *
 * @author Petteri Kivimäki
 */
public class TestServiceRequest {

    /**
     * Response body character count.
     */
    private String responseBodySize;
    /**
     * Response attachment character count.
     */
    private String responseAttachmentSize;
    /**
     * Request payload string.
     */
    private String requestPayload;
    /**
     * Request attachment string.
     */
    private String requestAttachment;

    /**
     * Constructs and initializes a new TestServiceRequest with the given
     * values.
     *
     * @param requestPayload         request payload as a String
     * @param requestAttachment      request attachment as a String
     * @param responseBodySize       response body character count
     * @param responseAttachmentSize response attachment character count
     */
    public TestServiceRequest(String requestPayload, String requestAttachment, String responseBodySize, String responseAttachmentSize) {
        this.responseBodySize = responseBodySize;
        this.responseAttachmentSize = responseAttachmentSize;
        this.requestPayload = requestPayload;
        this.requestAttachment = requestAttachment;
    }

    /**
     * Returns the response body character count.
     *
     * @return esponseBodySize response body character count
     */
    public String getResponseBodySize() {
        return responseBodySize;
    }

    /**
     * Sets the response body character count.
     *
     * @param responseBodySize new value
     */
    public void setResponseBodySize(String responseBodySize) {
        this.responseBodySize = responseBodySize;
    }

    /**
     * Returns the response attachment character count.
     *
     * @return responseAttachmentSize response attachment character count
     */
    public String getResponseAttachmentSize() {
        return responseAttachmentSize;
    }

    /**
     * Sets the response attachment character count.
     *
     * @param responseAttachmentSize new value
     */
    public void setResponseAttachmentSize(String responseAttachmentSize) {
        this.responseAttachmentSize = responseAttachmentSize;
    }

    /**
     * Returns the request payload as a String.
     *
     * @return requestPayload request payload as a String
     */
    public String getRequestPayload() {
        return requestPayload;
    }

    /**
     * Sets the request payload String.
     *
     * @param requestPayload new value
     */
    public void setRequestPayload(String requestPayload) {
        this.requestPayload = requestPayload;
    }

    /**
     * Returns the request attachment as a String.
     *
     * @return the requestAttachment request attachment as a String
     */
    public String getRequestAttachment() {
        return requestAttachment;
    }

    /**
     * Sets the request attachment String.
     *
     * @param requestAttachment new value
     */
    public void setRequestAttachment(String requestAttachment) {
        this.requestAttachment = requestAttachment;
    }
}
