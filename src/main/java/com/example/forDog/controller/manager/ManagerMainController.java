package com.example.forDog.controller.manager;

import com.example.forDog.dto.NoticeDTO;
import com.example.forDog.service.MediaVideoService;
import com.example.forDog.service.MemberService;
import com.example.forDog.service.NoticeService;
import com.example.forDog.service.ShelterService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/manager")
public class ManagerMainController {

    private final NoticeService noticeService;
    private final ShelterService shelterService;
    private final MemberService memberService;
    private final MediaVideoService mediaVideoService;

    @GetMapping({"", "/"})
    public String managerMain(NoticeDTO noticeDTO, Model model, HttpSession httpSession) {

        // 간단히 로그인 체크
        Object admin = httpSession.getAttribute("admin");
        if (admin == null) {
            return "redirect:/manager/login";
        }

        List<NoticeDTO> noticeList = noticeService.getSelectTop3(noticeDTO);
        // 카운트 통계(TJ)
        long countShelter = shelterService.countShelter();
        long countMember = memberService.countMember();
        long countVideo = mediaVideoService.countVideo();

        model.addAttribute("countMember",countMember);
        model.addAttribute("countVideo",countVideo);
        model.addAttribute("countShelter", countShelter);
        model.addAttribute("noticeList",noticeList);
        return "manager/main";
    }

}
