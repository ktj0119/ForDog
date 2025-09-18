package com.example.forDog.service;

public interface MailService {

    void sendMail(String email);
    boolean verifyCode(String email, int code);

}
