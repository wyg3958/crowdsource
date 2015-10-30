package de.asideas.crowdsource.controller;

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

    @Value("${de.asideas.crowdsource.content.application.name}")
    private String applicationName;

    @Value("${de.asideas.crowdsource.content.company.name}")
    private String companyName;

    @Cacheable
    @RequestMapping(value = "", method = RequestMethod.GET)
    public Map<String, String> content() {
        Map<String, String> result = new HashMap<>();
        result.put("de.asideas.crowdsource.content.application.name", applicationName);
        result.put("de.asideas.crowdsource.content.company.name", companyName);
        return result;
    }
}
