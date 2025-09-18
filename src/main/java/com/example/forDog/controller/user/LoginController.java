package com.example.forDog.controller.user;

import com.example.forDog.dto.MemberDTO;
import com.example.forDog.service.MemberService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("login/")
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;
    private String folderName = "user/login/";

    @RequestMapping("")
    public String login() {
        return folderName + "login";
    }

    @RequestMapping("loginProc")
    public String loginProc(
            HttpSession httpSession,
            MemberDTO memberDTO
    ) {
        String id = memberDTO.getId();
        String pwd = memberDTO.getPwd();

        memberDTO = memberService.getSelectOne(id);

        if (memberDTO == null) {
            return "redirect:/login/loginFail";
        }

        if (!pwd.equals(memberDTO.getPwd())) {
            return "redirect:/login/loginFail";
        }

        httpSession.setAttribute("memberNo", memberDTO.getNo());

        return "redirect:/";
    }

    @RequestMapping("/loginFail")
    public String loginFail() {
        return folderName + "loginFail";
    }

    @RequestMapping("/logout")
    public String logout(
            HttpSession session
    ) {
        session.invalidate();

        return folderName + "logout";
    }

    @RequestMapping("terms")
    public String terms() {
        return folderName + "terms";
    }

    @RequestMapping("register")
    public String register() {
        return folderName + "register";
    }

    // 이메일 인증 (모달)
    @RequestMapping("registerCheck")
    @ResponseBody
    public boolean registerCheck(
            @RequestParam(value = "id") String id
    ) {
        return memberService.getSelectId(id);
    }

    @RequestMapping(value = "registerProc", method = RequestMethod.POST)
    public String registerProc(MemberDTO memberDTO) {
        memberService.setInsert(memberDTO);

        return "redirect:/login/completed";
    }

    @RequestMapping("completed")
    public String completed() {
        return folderName + "completed";
    }

    @RequestMapping("findId")
    public String findId() {
        return folderName + "findId";
    }

    @RequestMapping("findPwd")
    public String findPwd() {
        return folderName + "findPwd";
    }

    @RequestMapping("pwdChange")
    public String pwdChange(
            Model model,
            @RequestParam(value = "id", required = false) String id
    ) {
        if (id == null || id.trim().isEmpty()) {
            return "redirect:/login/findPwd";
        }

        MemberDTO memberDTO = memberService.getSelectOne(id);

        model.addAttribute("memberDTO", memberDTO);

        return folderName + "pwdChange";
    }

    @RequestMapping("pwdChangeProc")
    public String pwdChangeProc(
            @RequestParam(value = "id", required = false) String id,
            @RequestParam(value = "pwd", required = false) String pwd
    ) {
        if (id == null || id.trim().isEmpty()) {
            return "redirect:/login/findPwd";
        }

        MemberDTO memberDTO = memberService.getSelectOne(id);
        memberDTO.setPwd(pwd);

        memberService.setUpdate(memberDTO);

        return "redirect:/mypage/enterRemoveFinish";
    }

}
