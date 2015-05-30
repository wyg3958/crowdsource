package de.axelspringer.ideas.crowdsource.example.restmail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.subethamail.wiser.Wiser;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/mails")
public class MailController {

    @Autowired
    private Wiser mailServer;

    @RequestMapping(method = RequestMethod.GET)
    public List<Message> messages() {
        return mailServer.getMessages()
                .stream()
                .map(Message::new)
                .collect(Collectors.toList());
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public void clearMessages() {
        mailServer.getMessages().clear();
    }
}
