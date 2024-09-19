package org.hdcola.carnet.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.hdcola.carnet.Entity.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EmailServiceTests {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        emailService = spy(new EmailService(mailSender, templateEngine));
    }

    @Test
    void testSendSimpleEmail() {
        String toEmail = "test@example.com";
        String subject = "Test Subject";
        String body = "Test Body";

        emailService.sendSimpleEmail(toEmail, subject, body);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getTo()).contains(toEmail);
        assertThat(sentMessage.getSubject()).isEqualTo(subject);
        assertThat(sentMessage.getText()).isEqualTo(body);
        assertThat(sentMessage.getFrom()).isEqualTo(emailService.getEmailFrom());
    }

    @Test
    void testSendThymeleafMail() throws Exception {
        String toEmail = "test@example.com";
        String subject = "Test Subject";
        String template = "email/welcome";
        Map<String, Object> templateModel = Map.of("key", "value");
        emailService.setEmailFrom("no-reply@hdcola.org");

        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
        doNothing().when(mailSender).send(mimeMessage);

        Context context = new Context();
        context.setVariables(templateModel);
        String htmlContent = "<html><body>Test Content</body></html>";
        when(templateEngine.process(eq(template), any(Context.class))).thenReturn(htmlContent);

        emailService.sendThymeleafMail(toEmail, subject, template, templateModel);

        ArgumentCaptor<MimeMessage> messageCaptor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        // TODO: Add assertions for the sent message
    }

    @Test
    void testSendWelcomeEmail() throws Exception {
        String toEmail = "test@example.com";
        String name = "Test User";
        Role role = Role.BUYER;

        // Mock the internal method calls
        doNothing().when(emailService).sendSimpleEmailToAdmin(anyString(), anyString());
        doNothing().when(emailService).sendThymeleafMail(anyString(), anyString(), anyString(), anyMap());

        // Call the method to test
        emailService.sendWelcomeEmail(toEmail, name, role);

        // Verify that the internal methods were called with the correct parameters
        verify(emailService, times(1)).sendSimpleEmailToAdmin(
                eq("New user registered: " + toEmail),
                eq(role.name() + " have a new user registered")
        );
        verify(emailService, times(1)).sendWelcomeEmail(toEmail, name);
    }


}