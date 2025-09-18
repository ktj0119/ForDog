package com.example.forDog.controller.user;

import com.example.forDog.dto.*;
import com.example.forDog.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("mypage/")
@RequiredArgsConstructor
public class MypageController {

    private final MemberService memberService;
    private final MediaVideoService mediaVideoService;
    private final MediaRegistrationService mediaRegistrationService;
    private final MediaVideoProcessService mediaVideoProcessService;
    private final MediaCompletionService mediaCompletionService;
    private final UnityHaechanService unityHaechanService;
    private String folderName = "user/mypage/";

    @RequestMapping("metaverse")
    public String metaverse(
            Model model,
            HttpSession httpSession
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        // 메타버스 게임 목록
        List<String> metaverseNameList = new ArrayList<>();
        metaverseNameList.add("Dahee");
        metaverseNameList.add("Haechan");

        Map<String, Object> daheeMap = memberService.getGameSaveDataProcess(memberNo);
        UnityHaechanDTO haechanDTO = unityHaechanService.getSelectOneInMember(memberNo);

        // 게임별 씬 이름 목록
        @SuppressWarnings("unchecked") // 불필요한 경고 제거
        List<String> daheeSceneList = (List<String>) daheeMap.get("sceneIndexName");
        List<String> haechanSceneList = unityHaechanService.getStageName();

        // 게임별 진행 단계 목록
        int daheeProcessStage = (Integer) daheeMap.get("topSceneIndex");
        int haechanProcessStage = 0;
        if (haechanDTO != null) {
            haechanProcessStage = haechanDTO.getUnlockedBooks();
        }

        System.out.println(daheeProcessStage +"  @@  " + haechanProcessStage);
        model.addAttribute("metaverseNameList", metaverseNameList);
        model.addAttribute("daheeSceneList", daheeSceneList);
        model.addAttribute("haechanSceneList", haechanSceneList);
        model.addAttribute("daheeProcessStage", daheeProcessStage);
        model.addAttribute("haechanProcessStage", haechanProcessStage);

        model.addAttribute("currentPage", "metaverse");

        return folderName + "metaverse";
    }

    @RequestMapping("course")
    public String course(
            Model model,
            HttpSession httpSession
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        List<MediaRegistrationDTO> mediaRegistrationList = mediaRegistrationService.getSelectAllInMember(memberNo);
        List<Integer> completionRateList = new ArrayList<>();
        List<MediaCompletionDTO> mediaCompletionList = new ArrayList<>();
        int completionRateCount = 0;
        int mediaCompletionCount = 0;

        for (MediaRegistrationDTO mediaRegistrationDTO : mediaRegistrationList) {
            int mediaGroupNo = mediaRegistrationDTO.getMediaGroup().getNo();

            List<MediaVideoDTO> mediaVideoList = mediaVideoService.getSelectAllInGroup(mediaGroupNo);
            List<MediaVideoProcessDTO> mediaVideoProcessList
                    = mediaVideoProcessService.getSelectAllInMemberAndGroup(memberNo, mediaGroupNo);
            MediaCompletionDTO mediaCompletionDTO = mediaCompletionService.getSelectOneInMemberAndGroup(memberNo, mediaGroupNo);

            int completionRate = mediaVideoProcessService.completionRateCalculator(mediaVideoList, mediaVideoProcessList);

            if (completionRate == 100) completionRateCount++;
            if (mediaCompletionDTO != null) mediaCompletionCount++;

            completionRateList.add(completionRate);
            mediaCompletionList.add(mediaCompletionDTO);
        }

        model.addAttribute("mediaRegistrationList", mediaRegistrationList);
        model.addAttribute("completionRateList", completionRateList);
        model.addAttribute("mediaCompletionList", mediaCompletionList);
        model.addAttribute("completionRateCount", completionRateCount);
        model.addAttribute("mediaCompletionCount", mediaCompletionCount);

        model.addAttribute("currentPage", "course");

        return folderName + "course";
    }

    @RequestMapping("print")
    public String print(
            Model model,
            HttpSession httpSession,
            @RequestParam("mediaGroupNo") int mediaGroupNo
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "user/edu/media/closePopup";
        }

        MediaCompletionDTO mediaCompletionDTO = mediaCompletionService.getSelectOneInMemberAndGroup(memberNo, mediaGroupNo);

        model.addAttribute("mediaCompletionDTO", mediaCompletionDTO);

        return folderName + "print";
    }

    @RequestMapping("enterPwd/{next}")
    public String enterPwd(
            Model model,
            HttpSession httpSession,
            @PathVariable("next") String next
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        model.addAttribute("next", next);

        if (next.equals("pwdChange")) {
            model.addAttribute("currentPage", "pwdChange");
        } else if (next.equals("memberEdit")){
            model.addAttribute("currentPage", "memberEdit");
        } else if (next.equals("remove")){
            model.addAttribute("currentPage", "remove");
        }

        return folderName + "enterPwd";
    }

    @RequestMapping("enterPwdProc")
    public String enterPwdProc(
            Model model,
            HttpSession httpSession,
            @RequestParam(value = "next") String next,
            @RequestParam(value = "pwd") String pwd
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        MemberDTO memberDTO = memberService.getSelectOne(memberNo);

        if (!pwd.equals(memberDTO.getPwd())){
            return "redirect:/mypage/enterFail/" + next;
        }

        return "redirect:/mypage/" + next;
    }

    @RequestMapping("enterFail/{next}")
    public String enterFail(
            Model model,
            HttpSession httpSession,
            @PathVariable("next") String next
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        model.addAttribute("next", next);

        return folderName + "enterFail";
    }

    @RequestMapping("enterFinish")
    public String enterFinish(
            Model model,
            HttpSession httpSession
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        model.addAttribute("memberNo", memberNo);

        return folderName + "enterFinish";
    }

    @RequestMapping("enterRemoveFinish")
    public String enterRemoveFinish(
            Model model
    ) {
        int memberNo = 0;

        model.addAttribute("memberNo", memberNo);

        return folderName + "enterFinish";
    }

    @RequestMapping("pwdChange")
    public String pwdChange(
            Model model,
            HttpSession httpSession
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        model.addAttribute("currentPage", "pwdChange");

        return folderName + "pwdChange";
    }

    @RequestMapping("pwdChangeProc")
    public String pwdChangeProc(
            HttpSession httpSession,
            @RequestParam(value = "pwd") String pwd
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        MemberDTO memberDTO = memberService.getSelectOne(memberNo);
        memberDTO.setPwd(pwd);

        memberService.setUpdate(memberDTO);

        return "redirect:/mypage/enterFinish";
    }

    @RequestMapping("memberEdit")
    public String memberEdit(
            Model model,
            HttpSession httpSession
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        MemberDTO memberDTO = memberService.getSelectOne(memberNo);

        model.addAttribute("memberDTO", memberDTO);

        model.addAttribute("currentPage", "memberEdit");

        return folderName + "memberEdit";
    }

    @RequestMapping("memberEditProc")
    public String memberEditProc(
            HttpSession httpSession,
            @RequestParam(value = "name") String name,
            @RequestParam(value = "gender") String gender,
            @RequestParam(value = "birthDate") Date birthDate,
            @RequestParam(value = "phone") String phone,
            @RequestParam(value = "addressMain") String addressMain,
            @RequestParam(value = "addressDetail") String addressDetail
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        MemberDTO memberDTO = memberService.getSelectOne(memberNo);
        memberDTO.setName(name);
        memberDTO.setGender(gender);
        memberDTO.setBirthDate(birthDate);
        memberDTO.setPhone(phone);
        memberDTO.setAddressMain(addressMain);
        memberDTO.setAddressDetail(addressDetail);

        memberService.setUpdate(memberDTO);

        return "redirect:/mypage/enterFinish";
    }

    @RequestMapping("remove")
    public String remove(
            Model model,
            HttpSession httpSession
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        model.addAttribute("currentPage", "remove");

        return folderName + "remove";
    }

    @RequestMapping("removeProc")
    public String removeProc(
            HttpSession httpSession
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        MemberDTO memberDTO = memberService.getSelectOne(memberNo);

        memberService.setDelete(memberDTO);
        httpSession.invalidate();

        return "redirect:/mypage/enterRemoveFinish";
    }

    private int sessionCheck(HttpSession httpSession) {
        Object memberObject = httpSession.getAttribute("memberNo");
        if (memberObject == null) {
            return -1;
        }
        return (Integer) memberObject;
    }

}
