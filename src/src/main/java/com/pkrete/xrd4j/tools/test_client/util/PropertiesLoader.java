package com.pkrete.xrd4j.tools.test_client.util;

import com.pkrete.xrd4j.common.util.PropertiesUtil;
import java.io.File;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class is responsiple of loading all the properties files.
 *
 * @author Petteri Kivimäki
 */
public class PropertiesLoader {

    private static final Logger logger = LoggerFactory.getLogger(PropertiesLoader.class);
    private static Properties generalSettings;
    private static Properties clientSettings;

    /**
     * Loads general settings file and returns properties found from
     * it.
     * @return general settings properties
     */
    public static Properties loadGeneralSettings() {
        if (generalSettings != null) {
            logger.trace("General settings already loaded. Use cached values.");
            return generalSettings;
        }
        logger.debug("Load general settings.");
        generalSettings = PropertiesLoader.load(Constants.GENERAL_SETTINGS_FILE);
        return generalSettings;
    }

    /**
     * Loads client settings file and returns properties found from
     * it.
     * @return client settings properties
     */
    public static Properties loadClientSettings() {
        if (clientSettings != null) {
            logger.trace("Client settings already loaded. Use cached values.");
            return clientSettings;
        }
        logger.debug("Load client settings.");
        clientSettings = PropertiesLoader.load(Constants.CLIENT_SETTINGS_FILE);
        return clientSettings;
    }

    /**
     * Loads properties from a file with the given filename. First the file
     * searched from the same directory with the jar file and then from
     * classpath.
     * @param fileName name of the file to be searched
     * @return properties loaded from the file
     */
    private static Properties load(String fileName) {
        logger.debug("Load settings.");
        Properties settings = null;
        String path = ApplicationHelper.getJarPath() + fileName;
        if (new File(path).exists()) {
            settings = PropertiesUtil.getInstance().load(path, false);
            if (settings != null) {
                logger.debug("Settings loaded from file \"{}\".", path);
                return settings;
            }
        }
        logger.debug("No external settings file was found from path \"{}\".", path);
        path = "/" + fileName;
        settings = PropertiesUtil.getInstance().load(path);
        logger.debug("Settings loaded from file \"{}\".", path);
        return settings;
    }
}
