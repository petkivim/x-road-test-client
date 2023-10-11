package com.pkrete.xroadtestclient.util;

/**
 * This class defines all the constants used in this application.
 *
 * @author Petteri Kivimäki
 */
public final class Constants {

    public static final String GENERAL_SETTINGS_FILE = "/settings.properties";
    public static final String CLIENT_SETTINGS_FILE = "/clients.properties";
    public static final String LOG4J_SETTINGS_FILE = "log4j.xml";
    public static final String CLIENT = "client";
    public static final String CLIENT_REQ_BODY_SIZE = "client.requestBodySize";
    public static final String CLIENT_REQ_ATTACH_SIZE = "client.requestAttachmentSize";
    public static final String CLIENT_RESPONSE_BODY_SIZE = "client.responseBodySize";
    public static final String CLIENT_RESPONSE_ATTACH_SIZE = "client.responseAttachmentSize";
    public static final String SERVICE = "service";
    public static final String SERVICE_NAMESPACE = "service.namespace";

    public static final String PROPERTIES_DIR_PARAM_NAME = "propertiesDirectory";

    /**
     * Constructs and initializes a new Constants object. Should never be used.
     */
    private Constants() {
    }
}
