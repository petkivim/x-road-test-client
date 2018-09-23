package com.pkrete.xroadtestclient.util;

import org.niis.xrd4j.common.exception.XRd4JException;
import org.niis.xrd4j.common.message.ServiceRequest;

import com.pkrete.xroadtestclient.request.TestServiceRequest;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.xml.DOMConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

/**
 * This class offers helper methods for the application.
 *
 * @author Petteri Kivimäki
 */
public final class ApplicationHelper {

    private static final Logger LOG = LoggerFactory.getLogger(ApplicationHelper.class);
    private static String jarDir;
    private static final int OS_NAME_LENGTH = 3;
    private static final int LINUX_LIMIT = 5;
    private static final int WIN_LIMIT = 6;

    /**
     * Constructs and initializes a new ApplicationHelper object. Should never
     * be used.
     */
    private ApplicationHelper() {

    }

    /**
     * Returns the absolute path of the jar file containing the application. The
     * path is returned with a trailing slash.
     *
     * @return absolute path of the current working directory
     */
    public static String getJarPath() {
        LOG.debug("Load jar directory.");
        if (jarDir != null && !jarDir.isEmpty()) {
            LOG.debug("Jar directory already loaded! Use cached value : \"{}\".", jarDir);
            return jarDir;
        }
        int limit = LINUX_LIMIT;
        if ("Win".equals(System.getProperty("os.name").substring(0, OS_NAME_LENGTH))) {
            limit = WIN_LIMIT;
        }
        String temp = ApplicationHelper.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(limit);
        String[] arr = temp.split("/");
        temp = temp.replace("%20", " ");
        try {
            jarDir = temp.replace(arr[arr.length - 1], "");
            jarDir = jarDir.replaceAll("/+$", "/");
            LOG.info("Jar directory loaded : \"{}\".", jarDir);
            return jarDir;
        } catch (Exception ex) {
            LOG.error(ex.getMessage(), ex);
            return null;
        }
    }

    /**
     * Loads Log4j configuration.
     */
    public static void configureLog4j() {
        LOG.debug("Configure Log4J.");
        String path = ApplicationHelper.getJarPath();
        String filePath = path + Constants.LOG4J_SETTINGS_FILE;
        File logConf = new File(filePath);
        if (logConf.exists()) {
            DOMConfigurator.configure(logConf.getAbsolutePath());
            LOG.debug("Logging configuration loaded from " + logConf.getAbsolutePath());
        } else {
            DOMConfigurator.configure(ApplicationHelper.class.getClassLoader().getResource(Constants.LOG4J_SETTINGS_FILE));
            LOG.warn("Couldn't find " + logConf.getAbsolutePath() + " configuration file. Use default configuration.");
        }
        LOG.debug("Loaded Log4J.");
    }

    /**
     * Returns a random string of given length.
     *
     * @param length length of the string
     * @return random string
     */
    public static String getRandomString(int length) {
        LOG.debug("Generate random string of {} charaters.", length);
        String s = RandomStringUtils.randomAlphanumeric(length);
        LOG.debug("String generated");
        return s;
    }

    /**
     * Copies the given ServiceRequest.
     *
     * @param request ServiceRequest to be copied
     * @return new ServiceRequest
     */
    public static ServiceRequest clone(ServiceRequest<TestServiceRequest> request) {
        try {
            // Create a new service request which request data type is String
            ServiceRequest<TestServiceRequest> newRequest = new ServiceRequest<>(request.getConsumer(), request.getProducer(), request.getId());
            // Set user id
            newRequest.setUserId(request.getUserId());
            // Set request data
            newRequest.setRequestData(request.getRequestData());
            // Return new request
            return newRequest;
        } catch (XRd4JException ex) {
            LOG.error(ex.getMessage(), ex);
        }
        return null;
    }

    /**
     * Parses the string argument as a signed decimal integer. If parsing of the
     * string fails, zero is returned.
     *
     * @param source a String containing the integer representation to be parsed
     * @return the integer value represented by the argument in decimal
     */
    public static int strToInt(String source) {
        try {
            return Integer.parseInt(source);
        } catch (NumberFormatException nfe) {
            return 0;
        }
    }

    /**
     * Converts the given milliseconds to string.
     *
     * @param milliseconds time to be converted
     * @return string
     */
    public static String millisecondsToString(long milliseconds) {
        int seconds = (int) (milliseconds / 1000) % 60;
        int minutes = (int) ((milliseconds / (1000 * 60)) % 60);
        int hours = (int) ((milliseconds / (1000 * 60 * 60)) % 24);
        return hours + " h " + minutes + " min " + seconds + " s";
    }
}
