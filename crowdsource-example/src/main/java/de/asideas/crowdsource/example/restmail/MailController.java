package de.asideas.crowdsource.example.restmail;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.subethamail.wiser.Wiser;
import org.subethamail.wiser.WiserMessage;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/mails")
public class MailController {

    private static final Logger log = LoggerFactory.getLogger(MailController.class);

    @Autowired
    private Wiser mailServer;

    @RequestMapping(method = RequestMethod.GET)
    public List<Message> messages() {
        final List<WiserMessage> messages = mailServer.getMessages();

        log.info("Serving {} E-Mails from locally mocked mail endpoint..", messages.size());
        return messages
                .stream()
                .map(Message::new)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void clearMessages() {
        log.info("Clearing E-Mails from locally mocked mail endpoint.");
        mailServer.getMessages().clear();
    }
}
