package com.personneltrackingsystem.service.Impl;

import com.personneltrackingsystem.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Override
    @Async
    public void sendSimpleMail(String to, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();

            message.setTo(to);
            message.setFrom("zcanarif@gmail.com");
            message.setSubject("Confirm your email");
            String body = """

                    Hello from Arif Özcan!
                    Please use the following link to verify your email:

                    http://localhost:8080/register/confirmToken?token=%s
                    """.formatted(token);
            message.setText(body);
            mailSender.send(message);
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalIdentifierException("Failed to send email");
        }
    }
}
