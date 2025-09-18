package com.example.forDog.controller.user;

import com.example.forDog.service.MailServiceGoogleImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MailController {

    private final MailServiceGoogleImpl googleMailService;

    @PostMapping("/mail/send")
    public String sendMail(@RequestParam String email) {
        try {
            googleMailService.sendMail(email);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "fail";
        }
    }

    @PostMapping("/mail/verify")
    public String verifyCode(@RequestParam String email, @RequestParam int code) {
        boolean verified = googleMailService.verifyCode(email, code);
        return String.valueOf(verified);
    }

}
