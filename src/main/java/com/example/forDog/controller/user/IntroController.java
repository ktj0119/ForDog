package com.example.forDog.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("intro/")
@RequiredArgsConstructor
public class IntroController {

    private String folderName = "user/intro/";

    @RequestMapping("introduce")
    public String introduce(
            Model model
    ) {
        model.addAttribute("currentPage", "introduce");

        return folderName + "introduce";
    }

    @RequestMapping("map")
    public String map(
            Model model
    ) {
        model.addAttribute("currentPage", "map");

        return folderName + "map";
    }

}
