package de.axelspringer.ideas.crowdsource.example.restmail;

import org.apache.commons.io.IOUtils;
import org.subethamail.wiser.WiserMessage;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

public class Message {

    private String from;

    private String to;

    private String subject;

    private String message;

    public Message(WiserMessage wiserMessage) {

        try {
            final MimeMessage mimeMessage = wiserMessage.getMimeMessage();
            from = InternetAddress.toString(mimeMessage.getFrom());
            to = InternetAddress.toString(mimeMessage.getAllRecipients());
            subject = mimeMessage.getSubject();
            message = IOUtils.toString(mimeMessage.getInputStream());
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
