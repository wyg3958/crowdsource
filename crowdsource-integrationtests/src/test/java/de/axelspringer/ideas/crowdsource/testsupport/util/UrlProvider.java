package de.axelspringer.ideas.crowdsource.testsupport.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlProvider {

    private Logger log = LoggerFactory.getLogger(getClass());

    @Value("${de.axelspringer.ideas.crowdsource.test.applicationhost:localhost}")
    private String applicationHost;

    @Value("${de.axelspringer.ideas.crowdsource.test.server.port:8080}")
    private Integer applicationPort;

    public String mailserverUrl() {

        final String mailserverUrl = "http://" + applicationHost + ":" + applicationPort + "/mails";
        log.debug("providing mailserver url ({})", mailserverUrl);
        return mailserverUrl;
    }

    public String applicationUrl() {

        final String applicationUrl = "http://" + applicationHost + ":" + applicationPort;
        log.debug("providing application url ({})", applicationUrl);
        return applicationUrl;
    }
}
