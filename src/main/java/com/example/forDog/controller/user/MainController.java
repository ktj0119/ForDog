package com.example.forDog.controller.user;

import com.example.forDog.dto.NoticeDTO;
import com.example.forDog.service.NoticeService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class MainController implements ErrorController {

    private final NoticeService noticeService;
    private String folderName = "user/main/";

    @RequestMapping({"", "/"})
    public String mainHome(
            Model model
    ) {
        List<NoticeDTO> noticeFiveList = noticeService.getSelectTop5();

        model.addAttribute("noticeFiveList", noticeFiveList);

        return folderName + "main";
    }

//    @RequestMapping("/error")
//    public String errorPage(HttpServletRequest request) {
//        Object status = request.getAttribute("javax.servlet.error.status_code");
//
//        if (status != null) {
//            Integer statusCode = Integer.valueOf(status.toString());
//
//            // 404 에러
//            if (statusCode == HttpStatus.NOT_FOUND.value()) {
//                return "header/error";
//            }
//        }
//
//        // 그 외 모든 에러 및 status가 null
//        return "header/error";
//    }

}
