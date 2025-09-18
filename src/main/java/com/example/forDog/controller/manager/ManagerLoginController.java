package com.example.forDog.controller.manager;

import com.example.forDog.dto.MemberDTO;
import com.example.forDog.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class ManagerLoginController {

    private final MemberService service;

    @GetMapping("/manager/login")
    public String managerLogin() {
        return "manager/login";
    }

    // 로그인 & 로그아웃 처리 추가(TJ)
    // 시큐리티 대신 httpSession에 no값을 할당시켜서 관리자 정보 저장
    // RedirectAttributes를 사용하여 redirect시 원하는 Attribute를 전송
    @RequestMapping("/manager/loginProc")
    public String managerLoginProc(HttpSession session, RedirectAttributes re, MemberDTO memberDTO) {

        MemberDTO adminDTO = service.getSelectOne(memberDTO.getId());
        // 0820 수정(TJ) - 아이디 오류 추가 및 최적화
        // 아이디가 없는 경우
        if(adminDTO == null || !(adminDTO.getId().equals(memberDTO.getId()))) {
            re.addFlashAttribute("error3","존재하지 않는 아이디입니다.");
            return "redirect:/manager/login";
        }

        // 0812 수정(TJ)
        // 관리자 : 1, 사용자 : 0의 값을 가지도록 설정
        // 관리자가 아닐 시 에러 메세지를 뷰에 보내기 → 뷰에서 알람을 통해 보여주기
        if (adminDTO.getAdmin() != 1) {
            re.addFlashAttribute("error1","관리자 권한이 없습니다.");
            return "redirect:/manager/login";
        }

        // 0812 수정(TJ)
        // 아이디가 일치하지 않거나 비밀번호가 일치하지 않을 경우 위와 같이 에러 메세지 보내기
        if (!(adminDTO.getPwd().equals(memberDTO.getPwd()))) {
            re.addFlashAttribute("error2","비밀번호가 일치하지 않습니다.");
            return "redirect:/manager/login";
        }

        session.setAttribute("admin",adminDTO.getNo());
        session.setAttribute("adminName",adminDTO.getName());
        return "redirect:/manager/";
    }

    // 로그아웃 시 세션 값 없애기
    @RequestMapping("/manager/logout")
    public String logout(HttpSession httpSession) {
        httpSession.invalidate();
        return "redirect:/manager/login";
    }

    @RequestMapping("/manager/regist")
    public String managerRegist() {
        return "manager/regist";
    }

    @RequestMapping("/manager/registProc")
    public String managerRegistProc(MemberDTO memberDTO) {
        service.setInsert(memberDTO);
        return "redirect:/manager/login";
    }
}
