package com.example.forDog.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class MailServiceGoogleImpl implements MailService {

    private final JavaMailSender javaMailSender;
    private static final String SENDER_EMAIL = "applemint1173@gmail.com";
    private static final Map<String, Integer> verificationCodes = new HashMap<>();

    private void createRandomCode(String email) {
        int code = new Random().nextInt(900000) + 100000;
        verificationCodes.put(email, code);
    }

    @Override
    public void sendMail(String email) {
        createRandomCode(email);
        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(SENDER_EMAIL);
            helper.setTo(email);
            helper.setSubject("[ForDog] 이메일 인증");
            helper.setText("<h3>인증번호:</h3><h1>" + verificationCodes.get(email) + "</h1>", true);
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        javaMailSender.send(message);
    }

    @Override
    public boolean verifyCode(String email, int code) {
        Integer storedCode = verificationCodes.get(email);
        return storedCode != null && storedCode == code;
    }

}
