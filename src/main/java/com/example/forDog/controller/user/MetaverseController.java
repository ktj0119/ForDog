package com.example.forDog.controller.user;


import com.example.forDog.dto.MemberDTO;
import com.example.forDog.dto.UnityHaechanDTO;
import com.example.forDog.service.MemberService;
import com.example.forDog.service.UnityHaechanService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

@Controller
@RequestMapping("metaverse/")
@RequiredArgsConstructor
public class MetaverseController {

    private final MemberService memberService;
    private final UnityHaechanService unityHaechanService;
    private String folderName = "user/metaverse/";

    @RequestMapping("description")
    public String description() {
        return folderName + "description";
    }

    @RequestMapping("dahee")
    public String dahee(
            Model model,
            HttpSession httpSession
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userClosePopup";
        }

        MemberDTO memberDTO = memberService.getSelectOne(memberNo);
        model.addAttribute("loggedInUserId", memberDTO.getId());

        return folderName + "dahee";
    }

    @RequestMapping("haechan")
    public String haechan(
            Model model,
            HttpSession httpSession
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userClosePopup";
        }

        model.addAttribute("memberNo", memberNo);

        return folderName + "haechan";
    }

    @PostMapping("haechan/save")
    public ResponseEntity<?> saveHaechanData(
            HttpSession httpSession,
            @RequestBody UnityHaechanDTO unityHaechanDTO
    ) {
        UnityHaechanDTO findDTO= unityHaechanService.getSelectOneInMember(unityHaechanDTO.getMemberNo());

        if (findDTO != null) {
            unityHaechanDTO.setNo(findDTO.getNo());
        }

        unityHaechanService.setUpdate(unityHaechanDTO);

        return ResponseEntity.ok("success");
    }

    @RequestMapping("haechan/load")
    public ResponseEntity<?> load(
            @RequestParam("memberNo") int memberNo
    ) {
        UnityHaechanDTO dto = unityHaechanService.getSelectOneInMember(memberNo);

        if (dto == null) {
            return ResponseEntity.ok().body("{}"); // 데이터 없으면 빈 JSON 반환
        }

        return ResponseEntity.ok(dto);
    }

    private int sessionCheck(HttpSession httpSession) {
        Object memberObject = httpSession.getAttribute("memberNo");
        if (memberObject == null) {
            return -1;
        }
        return (Integer) memberObject;
    }

    // 비동기 세션 체킹
    @RequestMapping("checkSession")
    public ResponseEntity<?> checkSession(HttpSession session) {
        Integer memberNo = (Integer) session.getAttribute("memberNo");
        if (memberNo == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("SESSION_EXPIRED");
        }
        return ResponseEntity.ok(Map.of("memberNo", memberNo));
    }

}
