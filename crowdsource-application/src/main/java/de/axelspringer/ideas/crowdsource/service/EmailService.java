package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.model.presentation.user.User;
import org.apache.velocity.app.VelocityEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.ui.velocity.VelocityEngineUtils;

import java.util.Map;

public class EmailService {

    @Autowired
    private VelocityEngine velocityEngine;

    @Autowired
    private JavaMailSender mailSender;

    public void send(User user, String subject, final Map<String, Object> templateVariables) {
        MimeMessagePreparator preparator = mimeMessage -> {
            MimeMessageHelper message = new MimeMessageHelper(mimeMessage);
            message.setTo(user.getEmail());
            message.setFrom("noreply@crowd.asideas.de");
            message.setSubject(subject);

            String body = VelocityEngineUtils.mergeTemplateIntoString(velocityEngine, "/email/activation.vm", "ISO-8859", templateVariables);

            message.setText(body, true);
        };
        mailSender.send(preparator);
    }
}
