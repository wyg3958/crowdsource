package de.axelspringer.ideas.crowdsource.testsupport.util;

import org.apache.commons.lang3.StringUtils;

public class HostUtils {
    private HostUtils() {
    }

    public static String getApplicationHost() {

        final String applicationHost = System.getProperty("HOST_IP");

        if (StringUtils.isNoneEmpty(applicationHost)) {
            System.out.println("Using external application host: " + applicationHost);
            return applicationHost;
        } else {
            System.out.println("Using localhost as application host.");
            return "localhost";
        }
    }
}
