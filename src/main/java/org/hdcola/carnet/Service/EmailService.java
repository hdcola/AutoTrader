package org.hdcola.carnet.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.hdcola.carnet.Entity.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Slf4j
@Service
public class EmailService {

    @Value("${carnet.admin.email}")
    @Getter
    @Setter
    private String adminEmail;

    @Value("${carnet.admin.notification.emailfrom}")
    @Getter
    @Setter
    private String emailFrom;

    private final JavaMailSender mailSender;

    private final TemplateEngine templateEngine;

    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }

    public void sendSimpleEmailToAdmin(String subject, String body) {
        sendSimpleEmail(adminEmail, subject, body);
    }

    public void sendThymeleafMailToAdmin(String subject, String template, Map<String, Object> templateModel) throws MessagingException {
        sendThymeleafMail(adminEmail, subject, template, templateModel);
    }

    public void sendSimpleEmail(String toEmail, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setText(body);
        message.setSubject(subject);
        message.setFrom(emailFrom);

        mailSender.send(message);
        log.debug("Email sent to: {} : {}\n{}", toEmail, subject, body);
    }

    public void sendThymeleafMail(String toEmail, String subject, String template, Map<String, Object> templateModel) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setTo(toEmail);
        helper.setSubject(subject);
        helper.setFrom(emailFrom);

        Context context = new Context();
        context.setVariables(templateModel);
        String html = templateEngine.process(template, context);
        helper.setText(html, true);

        mailSender.send(message);
        log.debug("Email sent to: {} : {}\n{}", toEmail, subject, html);
    }


    public void sendWelcomeEmail(String toEmail, String name, Role role){
        sendSimpleEmailToAdmin("New user registered: " + toEmail, role.name() + " have a new user registered");
        sendWelcomeEmail(toEmail, name);
    }

    void sendWelcomeEmail(String toEmail, String name) {
        String subject = "Welcome to Carnet";
        String template = "email/welcome";
        Map<String, Object> templateModel = Map.of(
                "title", name,
                "message", "Welcome to Carnet"
        );
        try {
            sendThymeleafMail(toEmail, subject, template, templateModel);
        } catch (MessagingException e) {
            log.error("Error sending email", e);
        }
    }
}
