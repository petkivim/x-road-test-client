package com.pkrete.xrd4j.tools.test_client.util;

import com.pkrete.xrd4j.common.exception.XRd4JException;
import com.pkrete.xrd4j.common.member.ConsumerMember;
import com.pkrete.xrd4j.common.member.ProducerMember;
import com.pkrete.xrd4j.common.message.ServiceRequest;
import com.pkrete.xrd4j.tools.test_client.request.TestServiceRequest;
import java.io.File;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.log4j.xml.DOMConfigurator;

/**
 * This class offers helper methods for the application.
 *
 * @author Petteri Kivimäki
 */
public class ApplicationHelper {

    private static final Logger logger = LoggerFactory.getLogger(ApplicationHelper.class);
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789 ";
    private static String jarDir;

    /**
     * Returns the absolute path of the jar file containing the application. The
     * path is returned with a trailing slash.
     *
     * @return absolute path of the current working directory
     */
    public static String getJarPath() {
        logger.debug("Load jar directory.");
        if (jarDir != null && !jarDir.isEmpty()) {
            logger.debug("Jar directory already loaded! Use cached value : \"{}\".", jarDir);
            return jarDir;
        }
        int limit = 5;
        if (System.getProperty("os.name").substring(0, 3).equals("Win")) {
            limit = 6;
        }
        String temp = ApplicationHelper.class.getProtectionDomain().getCodeSource().getLocation().toString().substring(limit);
        String[] arr = temp.split("/");
        temp = temp.replace("%20", " ");
        try {
            jarDir = temp.replace(arr[arr.length - 1], "");
            jarDir = jarDir.replaceAll("/+$", "/");
            logger.info("Jar directory loaded : \"{}\".", jarDir);
            return jarDir;
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return null;
        }
    }

    /**
     * Loads Log4j configuration.
     */
    public static void configureLog4j() {
        logger.debug("Configure Log4J.");
        String path = ApplicationHelper.getJarPath();
        String filePath = path + Constants.LOG4J_SETTINGS_FILE;
        File logConf = new File(filePath);
        if (logConf.exists()) {
            DOMConfigurator.configure(logConf.getAbsolutePath());
            logger.debug("Logging configuration loaded from " + logConf.getAbsolutePath());
        } else {
            DOMConfigurator.configure(ApplicationHelper.class.getClassLoader().getResource(Constants.LOG4J_SETTINGS_FILE));
            logger.warn("Couldn't find " + logConf.getAbsolutePath() + " configuration file. Use default configuration.");
        }
        logger.debug("Loaded Log4J.");
    }

    /**
     * Returns a random string of given length.
     *
     * @param length length of the string
     * @return random string
     */
    public static String getRandomString(int length) {
        logger.debug("Generate random string of {} charaters.", length);
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        while (sb.toString().getBytes().length < length) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        logger.debug("String generated.");
        return sb.toString();
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
            ServiceRequest<TestServiceRequest> newRequest = new ServiceRequest<TestServiceRequest>(request.getConsumer(), request.getProducer(), request.getId());
            // Set user id
            newRequest.setUserId(request.getUserId());
            // Set request data
            newRequest.setRequestData(request.getRequestData());
            // Return new request
            return newRequest;
        } catch (XRd4JException e) {
            logger.debug(e.getMessage());
        }
        return null;
    }

    /**
     * Copies the client id string into an array. [0] = instance, [1] =
     * memberClass, [2] = memberCode, [3] = subsystem, [4] = service, [5] =
     * version. If the structure of the string is not correct, null is returned.
     *
     * @param serviceId service id string
     * @return service id in an array
     */
    private static String[] serviceIdToArr(String serviceId) {
        if (serviceId == null) {
            return null;
        }
        String[] serviceArr = serviceId.split("\\.");
        if (serviceArr.length >= 4 && serviceArr.length <= 6) {
            return serviceArr;
        }
        return null;
    }

    /**
     * Parses the given service id string and creates a new ProducerMember
     * according to its value. Null is returned if the given string doesn't
     * contain a valid service id.
     *
     * @param serviceId String containing a service id
     * @return new ProducerMember object or null
     */
    public static ProducerMember parseProducerMember(String serviceId) {
        String[] serviceIdArr = ApplicationHelper.serviceIdToArr(serviceId);
        if (serviceIdArr == null) {
            logger.warn("Service can not be null.");
            return null;
        } else {
            try {
                ProducerMember producer = null;
                String instance = serviceIdArr[0];
                String memberClass = serviceIdArr[1];
                String memberCode = serviceIdArr[2];
                if (serviceIdArr.length == 4) {
                    String service = serviceIdArr[3];
                    producer = new ProducerMember(instance, memberClass, memberCode, service);
                    logger.debug("Producer member succesfully created. Identifier format : \"instance.memberClass.memberCode.service\".");
                } else if (serviceIdArr.length == 5) {
                    // Last element is considered as version number if it
                    // starts with the letters [vV] and besides that contains
                    // only numbers, or if the element contains only numbers.
                    // Also characters [-_] are allowed.
                    if (serviceIdArr[4].matches("(v|V|)[\\d_-]+")) {
                        String service = serviceIdArr[3];
                        String version = serviceIdArr[4];
                        producer = new ProducerMember(instance, memberClass, memberCode, "subsystem", service, version);
                        producer.setSubsystemCode(null);
                        logger.debug("Producer member succesfully created. Identifier format : \"instance.memberClass.memberCode.service.version\".");
                    } else {
                        String subsystem = serviceIdArr[3];
                        String service = serviceIdArr[4];
                        producer = new ProducerMember(instance, memberClass, memberCode, subsystem, service);
                        logger.debug("Producer member succesfully created. Identifier format : \"instance.memberClass.memberCode.subsystem.service\".");
                    }
                } else if (serviceIdArr.length == 6) {
                    String subsystem = serviceIdArr[3];
                    String service = serviceIdArr[4];
                    String version = serviceIdArr[5];
                    producer = new ProducerMember(instance, memberClass, memberCode, subsystem, service, version);
                    logger.debug("Producer member succesfully created. Identifier format : \"instance.memberClass.memberCode.subsystem.service.version\".");
                }
                return producer;
            } catch (Exception ex) {
                logger.warn("Creating producer member failed.");
                return null;
            }
        }
    }

    /**
     * Copies the client id string into an array. [0] = instance, [1] =
     * memberClass, [2] = memberCode, [3] = subsystem. If the structure of the
     * string is not correct, null is returned.
     *
     * @param clientId client id string
     * @return client id in an array
     */
    private static String[] clientIdToArr(String clientId) {
        if (clientId == null) {
            return null;
        }
        String[] clientArr = clientId.split("\\.");
        if (clientArr.length == 3 || clientArr.length == 4) {
            return clientArr;
        }
        return null;
    }

    /**
     * Parses the given client id string and creates a new ConsumerMember
     * according to its value. Null is returned if the given string doesn't
     * contain a valid client id.
     *
     * @param clientId String containing a client id
     * @return new ProducerMember object or null
     */
    public static ConsumerMember parseConsumerMember(String clientId) {
        String[] clientIdArr = ApplicationHelper.clientIdToArr(clientId);
        if (clientIdArr == null) {
            logger.warn("Client can not be null.");
            return null;
        } else {
            try {
                ConsumerMember consumer = null;
                String instance = clientIdArr[0];
                String memberClass = clientIdArr[1];
                String memberCode = clientIdArr[2];
                if (clientIdArr.length == 3) {
                    consumer = new ConsumerMember(instance, memberClass, memberCode);
                    logger.debug("Consumer member succesfully created. Identifier format : \"instance.memberClass.memberCode\".");
                } else if (clientIdArr.length == 4) {
                    String subsystem = clientIdArr[3];
                    consumer = new ConsumerMember(instance, memberClass, memberCode, subsystem);
                    logger.debug("Consumer member succesfully created. Identifier format : \"instance.memberClass.memberCode.subsystem\".");
                }
                return consumer;
            } catch (Exception ex) {
                logger.warn("Creating consumer member failed.");
                return null;
            }
        }
    }
}
