package de.axelspringer.ideas.crowdsource.service;

public class UserActivationServiceIT {

    //TODO: cucumberize
//    private static final int SMTP_PORT = 10025;
//    private static final String RECIPIENT_ADRESS = "test@test.de";
//    private static final String SENDER_ADDRESS = "test@crowdsource.de";
//    private static final String APP_URL = "some.adress.de";
//
//    private final UserActivationService userActivationService = new UserActivationService();
//
//    private Wiser inMemorySMTPServer;
//
//    @Before
//    public void injectDependencies() {
//
//        ReflectionTestUtils.setField(userActivationService, "fromAddress", SENDER_ADDRESS);
//        ReflectionTestUtils.setField(userActivationService, "applicationUrl", APP_URL);
//
//        final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
//        mailSender.setHost("localhost");
//        mailSender.setPort(SMTP_PORT);
//
//        ReflectionTestUtils.setField(userActivationService, "mailSender", mailSender);
//    }
//
//    @Before
//    public void startMailServer() {
//        inMemorySMTPServer = new Wiser();
//        inMemorySMTPServer.setPort(SMTP_PORT);
//        inMemorySMTPServer.start();
//    }
//
//    @After
//    public void killMailServer() {
//        inMemorySMTPServer.stop();
//    }
//
//    @Test
//    public void testSendActivationMail() throws Exception {
//
//        userActivationService.sendActivationMail(new UserEntity(RECIPIENT_ADRESS));
//
//        assertThat(inMemorySMTPServer.getMessages(), hasSize(1));
//
//        MimeMessage message = inMemorySMTPServer.getMessages().get(0).getMimeMessage();
//
//        assertThat(message.getSubject(), is("CrowdSource Registrierung"));
//        assertThat(message.getAllRecipients(), arrayContaining(new InternetAddress(RECIPIENT_ADRESS)));
//        assertThat(message.getFrom(), arrayContaining(new InternetAddress(SENDER_ADDRESS)));
//
//        String content = IOUtils.toString(message.getInputStream());
//        assertThat(content, startsWith(UserActivationService.MAIL_CONTENT));
//    }
//
//    @Test
//    public void testActivationMailContainsActivationLink() throws MessagingException, IOException {
//
//        UserEntity user = new UserEntity(RECIPIENT_ADRESS);
//        user.setActivationToken("activationToken");
//        userActivationService.sendActivationMail(user);
//
//        assertThat(inMemorySMTPServer.getMessages(), hasSize(1));
//
//
//        MimeMessage message = inMemorySMTPServer.getMessages().get(0).getMimeMessage();
//        String content = IOUtils.toString(message.getInputStream());
//        final String registrationLink = content.substring(UserActivationService.MAIL_CONTENT.length()).trim();
//
//        String baseActivationUrl = APP_URL + "#/signup/" + RECIPIENT_ADRESS + "/activation/";
//        assertEquals(registrationLink, baseActivationUrl + user.getActivationToken());
//    }
}