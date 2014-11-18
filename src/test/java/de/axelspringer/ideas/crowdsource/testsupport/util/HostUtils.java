package de.axelspringer.ideas.crowdsource.testsupport.util;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostUtils {

    private final static Logger LOG = LoggerFactory.getLogger(HostUtils.class);

    private HostUtils() {
    }

    public static String getApplicationHost() {

        final String applicationHost = System.getProperty("HOST_IP");

        if (StringUtils.isNoneEmpty(applicationHost)) {
            LOG.debug("Using external application host: " + applicationHost);
            return applicationHost;
        } else {
            LOG.debug("Using localhost as application host.");
            return "localhost";
        }
    }
}
