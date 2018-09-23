package com.pkrete.xroadtestclient.util;

import org.niis.xrd4j.common.util.PropertiesUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Properties;

/**
 * This class is responsiple of loading all the properties files.
 *
 * @author Petteri Kivimäki
 */
public final class PropertiesLoader {

    private static final Logger LOG = LoggerFactory.getLogger(PropertiesLoader.class);
    private static Properties generalSettings;
    private static Properties clientSettings;

    /**
     * Constructs and initializes a new PropertiesLoader object. Should never be
     * used.
     */
    private PropertiesLoader() {
    }

    /**
     * Loads general settings file and returns properties found from it.
     *
     * @return general settings properties
     */
    public static Properties loadGeneralSettings() {
        if (generalSettings != null) {
            LOG.trace("General settings already loaded. Use cached values.");
            return generalSettings;
        }
        LOG.debug("Load general settings.");
        generalSettings = PropertiesLoader.load(Constants.GENERAL_SETTINGS_FILE);
        return generalSettings;
    }

    /**
     * Loads client settings file and returns properties found from it.
     *
     * @return client settings properties
     */
    public static Properties loadClientSettings() {
        if (clientSettings != null) {
            LOG.trace("Client settings already loaded. Use cached values.");
            return clientSettings;
        }
        LOG.debug("Load client settings.");
        clientSettings = PropertiesLoader.load(Constants.CLIENT_SETTINGS_FILE);
        return clientSettings;
    }

    /**
     * Loads properties from a file with the given filename. First the file
     * searched from the same directory with the jar file and then from class
     * path.
     *
     * @param fileName name of the file to be searched
     * @return properties loaded from the file
     */
    private static Properties load(String fileName) {
        LOG.debug("Load settings.");
        Properties settings;
        String path = ApplicationHelper.getJarPath() + fileName;
        if (new File(path).exists()) {
            settings = PropertiesUtil.getInstance().load(path, false);
            if (settings != null) {
                LOG.debug("Settings loaded from file \"{}\".", path);
                return settings;
            }
        }
        LOG.debug("No external settings file was found from path \"{}\".", path);
        path = "/" + fileName;
        settings = PropertiesUtil.getInstance().load(path);
        LOG.debug("Settings loaded from file \"{}\".", path);
        return settings;
    }
}
