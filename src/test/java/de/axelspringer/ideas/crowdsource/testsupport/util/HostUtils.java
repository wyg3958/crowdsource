package de.axelspringer.ideas.crowdsource.testsupport.util;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by sweller on 12.11.14.
 */
public class HostUtils {
    private HostUtils() {
    }

    public static final String getApplicationHost() {
        final String applicationHost = System.getProperty("applicationhost");

        if (StringUtils.isNoneEmpty(applicationHost)) {
            System.out.println("Using external application host: " + applicationHost);
            return applicationHost;
        } else {
            System.out.println("Using localhost as application host.");
            return "localhost";
        }
    }
}
