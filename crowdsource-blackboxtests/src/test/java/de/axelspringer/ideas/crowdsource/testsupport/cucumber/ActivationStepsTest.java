package de.axelspringer.ideas.crowdsource.testsupport.cucumber;

import de.axelspringer.ideas.crowdsource.service.UserService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link de.axelspringer.ideas.crowdsource.testsupport.cucumber.ActivationSteps}
 */
public class ActivationStepsTest {

    @Test
    public void testExtractActivationToken() {

        final String activationToken = RandomStringUtils.randomAlphanumeric(UserService.ACTIVATION_TOKEN_LENGTH);
        final String testMail = "Hallo Hans Dampf, Du hast Dich gerade auf der AS ideas Crowd Platform angemeldet. Um Deine Registrierung abzuschließen, öffne bitte diesen Link und setze Dein Passwort: http://localhost:8080#/signup/hans.dampf@axelspringer.de/activation/"
                + activationToken
                + " Bei Fragen wende dich an: support@crowd.asideas.de";

        assertEquals(activationToken, new ActivationSteps().extractActivationTokenFromMessage(testMail));
    }
}
