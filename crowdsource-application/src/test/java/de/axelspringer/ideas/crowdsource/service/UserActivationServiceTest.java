package de.axelspringer.ideas.crowdsource.service;

import de.axelspringer.ideas.crowdsource.model.persistence.UserEntity;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class UserActivationServiceTest {

    private static final String EMAIL = "some@mail.de";
    private static final String APP_URL = "http://test.de";

    @Mock
    private JavaMailSender javaMailSender;

    @InjectMocks
    private UserActivationService userActivationService;

    @Before
    public void init() {
        ReflectionTestUtils.setField(userActivationService, "applicationUrl", APP_URL);
    }

    @Test
    public void testSendActivationMail() {

        final UserEntity user = new UserEntity(EMAIL);
        userActivationService.sendActivationMail(user);
        verify(javaMailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}