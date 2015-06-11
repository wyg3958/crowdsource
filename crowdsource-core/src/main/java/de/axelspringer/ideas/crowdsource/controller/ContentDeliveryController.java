package de.axelspringer.ideas.crowdsource.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/content")
public class ContentDeliveryController {

    @Value("${de.axelspringer.ideas.crowdsource.content.application.name}")
    private String applicationName;

    @Value("${de.axelspringer.ideas.crowdsource.content.company.name}")
    private String companyName;

    @Value("${de.axelspringer.ideas.crowdsource.content.allowed.email.domain}")
    private String allowedEmailDomain;

    @Value("${de.axelspringer.ideas.crowdsource.content.email.blacklist.patterns}")
    private String emailBlacklistPatterns;

    @Cacheable
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Map<String, String> content() {
        Map<String, String> result = new HashMap<>();
        result.put("de.axelspringer.ideas.crowdsource.content.application.name", applicationName);
        result.put("de.axelspringer.ideas.crowdsource.content.company.name", companyName);
        result.put("de.axelspringer.ideas.crowdsource.content.allowed.email.domain", allowedEmailDomain);
        result.put("de.axelspringer.ideas.crowdsource.content.email.blacklist.patterns", allowedEmailDomain);
        result.put("de.axelspringer.ideas.crowdsource.content.email.blacklist.patterns", emailBlacklistPatterns);
        return result;
    }

    public static class Entry {
        public String key;
        public String value;

        public Entry(String key, String value) {
            this.key = key;
            this.value = value;
        }
    }
}
