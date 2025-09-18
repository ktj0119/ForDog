package com.example.forDog.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("header/")
@RequiredArgsConstructor
public class HeaderController {

    @RequestMapping("userClosePopup")
    public String userClosePopup() {
        return "header/userClosePopup";
    }

}
