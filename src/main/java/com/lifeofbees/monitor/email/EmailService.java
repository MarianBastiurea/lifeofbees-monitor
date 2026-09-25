package com.lifeofbees.monitor.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String emailAddress;

    public EmailService(
            JavaMailSender mailSender,
            @Value("${spring.mail.username}") String emailAddress) {

        this.mailSender = mailSender;
        this.emailAddress = emailAddress;
    }

    public void sendAlert(String subject, String text) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(emailAddress);
        message.setTo("marian.bastiurea@gmail.com");
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }
}