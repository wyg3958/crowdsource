package de.asideas.crowdsource.testsupport.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlProvider {

    private Logger LOG = LoggerFactory.getLogger(getClass());

    @Value("${de.asideas.crowdsource.test.applicationhost:localhost}")
    private String applicationHost;

    @Value("${de.asideas.crowdsource.test.server.port:8080}")
    private Integer applicationPort;

    public String mailserverUrl() {

        final String mailserverUrl = "http://" + applicationHost + ":" + applicationPort + "/mails";
        LOG.debug("providing mailserver url ({})", mailserverUrl);
        return mailserverUrl;
    }

    public String applicationUrl() {

        final String applicationUrl = "http://" + applicationHost + ":" + applicationPort;
        LOG.debug("providing application url ({})", applicationUrl);
        return applicationUrl;
    }
}
