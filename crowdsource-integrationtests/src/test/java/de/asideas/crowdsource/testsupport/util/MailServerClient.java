package de.asideas.crowdsource.testsupport.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class MailServerClient {

    @Autowired
    private UrlProvider urlProvider;

    private RestTemplate restTemplate = new RestTemplate();

    public void clearMails() {
        restTemplate.delete(urlProvider.mailserverUrl());
    }

    public void waitForMails(int mailCount, long wait) {

        long start = System.currentTimeMillis();
        String url = urlProvider.mailserverUrl();
        while (messages(url).size() < mailCount && System.currentTimeMillis() < start + wait) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private List<Message> messages(String url) {
        final Message[] messages = restTemplate.getForObject(url, Message[].class);
        return Arrays.asList(messages);
    }

    public List<Message> messages() {
        return messages(urlProvider.mailserverUrl());
    }

    public static class Message {

        public String from;

        public String to;

        public String subject;

        public String message;
    }
}
