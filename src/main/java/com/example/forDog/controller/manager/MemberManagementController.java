package com.example.forDog.controller.manager;

import com.example.forDog.dto.*;
import com.example.forDog.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/manager/member")
@RequiredArgsConstructor
public class MemberManagementController {

    private final MediaRegistrationService registrationService;
    private final MediaCompletionService completionService;
    private final MediaVideoProcessService processService;
    private final MediaVideoService videoService;
    private final MemberService service;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "searchType", required = false) String searchType,
                       @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

        Page<MemberDTO> pageList = service.getSelectAll(page, searchType, searchKeyword);

        if(searchType == null) searchType = "";
        if(searchKeyword == null) searchKeyword = "";

        model.addAttribute("list", pageList);
        model.addAttribute("paging", pageList);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        return "manager/member/list";
    }

    @GetMapping("/view/{no}")
    public String view(MemberDTO memberDTO, Model model) {
        // 사용자 한 사람 정보
        MemberDTO returnDTO = service.getSelectOne(memberDTO);
        // 사용자 한 사람의 수강신청 내역
        List<MediaRegistrationDTO> regiList = registrationService.getSelectInMember(memberDTO);
        // 사용자 한 사람의 수료 내역
        List<MediaCompletionDTO> completList = completionService.getSelectInMember(memberDTO);

        // 수강 신청한 미디어 동영상 진행도 목록
        Map<Integer, List<MediaVideoProcessDTO>> videoProcessMap = new HashMap<>();

        for (MediaRegistrationDTO regi : regiList) {
            int groupNo = regi.getMediaGroup().getNo();

            List<MediaVideoProcessDTO> processList = processService.getSelectAllInMemberAndGroup(memberDTO.getNo(), groupNo);

            List<MediaVideoDTO> allVideos = videoService.getSelectAllInGroup(groupNo);

            Set<Integer> existingVideoNos = processList.stream()
                    .map(p -> p.getMediaVideo() != null ? p.getMediaVideo().getNo() : null)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // 없는 비디오에 대해 진행도 0 DTO 생성
            for (MediaVideoDTO video : allVideos) {
                if (!existingVideoNos.contains(video.getNo())) {
                    MediaVideoProcessDTO zeroDto = new MediaVideoProcessDTO();
                    zeroDto.setPlayTime(0);
                    zeroDto.setMediaVideo(video);
                    processList.add(zeroDto);
                }
            }

            // 정렬
            processList.sort(Comparator.comparingInt(p -> p.getMediaVideo() != null ? p.getMediaVideo().getNo() : 0));

            videoProcessMap.put(groupNo, processList);
        }

        model.addAttribute("regiList", regiList);
        model.addAttribute("completList", completList);
        model.addAttribute("returnDTO", returnDTO);
        model.addAttribute("videoProcessMap",videoProcessMap);
        return "manager/member/view";
    }

    @GetMapping("/compltePrint/{no}")
    public String printCertificate(MediaCompletionDTO mediaCompletionDTO, Model model) {
        // completionId로 DTO 조회 (예: DB에서)
        MediaCompletionDTO dto = completionService.getSelectOne(mediaCompletionDTO);
        if (dto == null) {
            throw new RuntimeException("수료 정보를 찾을 수 없습니다.");
        }

        model.addAttribute("mediaCompletionDTO", dto);
        return "manager/member/compltePrint";
    }


    @GetMapping("/chuga")
    public String chuga() {
        return "manager/member/chuga";
    }

    @GetMapping("/infoSujung/{no}")
    public String infoSujung(MemberDTO memberDTO, Model model) {
        MemberDTO returnDTO = service.getSelectOne(memberDTO);
        model.addAttribute("returnDTO", returnDTO);
        return "manager/member/infoSujung";
    }

    @GetMapping("/pwdSujung/{no}")
    public String pwdSujung(MemberDTO memberDTO, Model model) {
        MemberDTO returnDTO = service.getSelectOne(memberDTO);
        model.addAttribute("returnDTO", returnDTO);
        return "manager/member/pwdSujung";
    }

    @RequestMapping("/chugaProc")
    public String chugaProc(MemberDTO memberDTO) {
        service.setInsert(memberDTO);
        return "redirect:/manager/member/list";
    }

    // 0812 수정(TJ)
    @RequestMapping("/infoSujungProc")
    public String infoSujungProc(MemberDTO memberDTO, RedirectAttributes re) {
        String pwd = service.getSelectOne(memberDTO).getPwd();
        if (pwd.equals(memberDTO.getPwd())) {
            service.setUpdate(memberDTO);
        } else {
            // 비밀번호 일치하지 않을 시 에러 메세지 설정(TJ)
            re.addFlashAttribute("error","비밀번호가 일치하지 않습니다.");
            return "redirect:/manager/member/infoSujung/" + memberDTO.getNo();
        }
        return "redirect:/manager/member/view/" + memberDTO.getNo();
    }

    @RequestMapping("/sakjeProc")
    public String sakjeProc(MemberDTO memberDTO) {
        service.setDelete(memberDTO);
        return "redirect:/manager/member/list";
    }
}
