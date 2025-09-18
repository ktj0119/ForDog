package com.example.forDog.controller.user;

import com.example.forDog.dto.*;
import com.example.forDog.service.*;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("edu/media/")
@RequiredArgsConstructor
public class EduMediaController {

    private final MediaGroupService mediaGroupService;
    private final MediaVideoService mediaVideoService;
    private final MediaQuizService mediaQuizService;
    private final MediaRegistrationService mediaRegistrationService;
    private final MediaVideoProcessService mediaVideoProcessService;
    private final MediaCompletionService mediaCompletionService;
    private final int quizNumber = 10;
    private final int correctNumber = 6;
    private String folderName = "user/edu/media/";

    @RequestMapping("list")
    public String list(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword
    ) {
        Page<MediaGroupDTO> pageList = mediaGroupService.getSelectFilterAll(quizNumber, page, searchKeyword);

        if(searchType == null) searchType = "";
        if(searchKeyword == null) searchKeyword = "";

        model.addAttribute("paging", pageList);
        model.addAttribute("totalCount", pageList.getTotalElements());

        model.addAttribute("page", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);

        model.addAttribute("currentPage", "media");

        return folderName + "list";
    }

    @RequestMapping("view/{groupNo}")
    public String view(
            Model model,
            HttpSession httpSession,
            @PathVariable("groupNo") int mediaGroupNo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword
    ) {
        MediaGroupDTO returnDTO = mediaGroupService.getSelectOne(mediaGroupNo);
        List<MediaVideoDTO> mediaVideoList = mediaVideoService.getSelectAllInGroup(mediaGroupNo);

        // 수강신청 여부 확인
        Object memberObject = httpSession.getAttribute("memberNo");

        if (memberObject != null) {
            int memberNo = (Integer) httpSession.getAttribute("memberNo");

            MediaRegistrationDTO checkRegisterDTO
                    = mediaRegistrationService.getSelectOneInMemberAndGroup(memberNo, mediaGroupNo);

            model.addAttribute("checkRegisterDTO", checkRegisterDTO);
        }

        if(searchType == null) searchType = "";
        if(searchKeyword == null) searchKeyword = "";

        model.addAttribute("returnDTO", returnDTO);
        model.addAttribute("mediaVideoList", mediaVideoList);

        model.addAttribute("page", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);

        model.addAttribute("currentPage", "media");

        return folderName + "view";
    }

    // 수강신청
    @RequestMapping("courseRegister")
    public String courseRegister(
            HttpSession httpSession,
            MediaRegistrationDTO mediaRegistrationDTO
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userSessionExpired";
        }

        mediaRegistrationDTO.setStatusDate(LocalDateTime.now());
        mediaRegistrationService.setInsert(mediaRegistrationDTO);

        return "redirect:/edu/media/view/" + mediaRegistrationDTO.getMediaGroup().getNo();
    }

    @RequestMapping("video/{groupNo}")
    public String video(
            Model model,
            HttpSession httpSession,
            @PathVariable("groupNo") int mediaGroupNo,
            MediaVideoDTO mediaVideoDTO
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userClosePopup";
        }

        // 1. 동영상 분류
        MediaGroupDTO mediaGroupDTO = mediaGroupService.getSelectOne(mediaGroupNo);

        // 2. 해당 동영상 분류의 모든 비디오 리스트
        List<MediaVideoDTO> mediaVideoList = mediaVideoService.getSelectAllInGroup(mediaGroupNo);

        // 3. 처음 팝업 열때 vs 동영상 목록 누른 후 재생
        if (mediaVideoDTO == null || mediaVideoDTO.getNo() == 0) {
            mediaVideoDTO = mediaVideoService.copyDTO(mediaVideoList.get(0));
        }

        if (mediaVideoDTO.getNo() != 0) {
            mediaVideoDTO = mediaVideoService.getSelectOne(mediaVideoDTO);
        }

        // 4. 사용자 번호랑 동영상 번호로 영상 진행도 찾기
        MediaVideoProcessDTO mediaVideoProcessDTO
                = mediaVideoProcessService.getSelectOneInMemberAndVideo(memberNo, mediaVideoDTO.getNo());

        // 5. 그룹별 영상 진행도 리스트와 이수율
        List<MediaVideoProcessDTO> mediaVideoProcessList
                = mediaVideoProcessService.getSelectAllInMemberAndGroup(memberNo, mediaGroupNo);

        int completionRate = mediaVideoProcessService.completionRateCalculator(mediaVideoList, mediaVideoProcessList);

        // 6. 수료증 발급 이후 시험접근 금지
        MediaCompletionDTO mediaCompletionDTO
                = mediaCompletionService.getSelectOneInMemberAndGroup(memberNo, mediaGroupNo);

        if (mediaCompletionDTO != null) {
            model.addAttribute("alreadyCompleted", true);
        }

        // 7. 전체 진행도 표출
        List<MediaVideoProcessDTO> allMediaProcess = new ArrayList<>();

        for (int i = 0; i < mediaVideoList.size(); i++) {
            MediaVideoProcessDTO findProcess
                    = mediaVideoProcessService.getSelectOneInMemberAndVideo(memberNo, mediaVideoList.get(i).getNo());

            if (findProcess == null) {
                findProcess = new MediaVideoProcessDTO();
                findProcess.setPlayTime(0);
            }

            allMediaProcess.add(findProcess);
        }

        model.addAttribute("mediaGroupDTO", mediaGroupDTO);
        model.addAttribute("mediaVideoDTO", mediaVideoDTO);
        model.addAttribute("mediaVideoList", mediaVideoList);
        model.addAttribute("mediaVideoProcessDTO", mediaVideoProcessDTO);
        model.addAttribute("mediaVideoProcessList", mediaVideoProcessList);
        model.addAttribute("completionRate", completionRate);
        model.addAttribute("allMediaProcess", allMediaProcess);

        return folderName + "video";
    }

    @RequestMapping("test/{groupNo}")
    public String test(
            Model model,
            HttpSession httpSession,
            @PathVariable("groupNo") int mediaGroupNo
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userClosePopup";
        }

        MediaGroupDTO mediaGroupDTO = mediaGroupService.getSelectOne(mediaGroupNo);

        try {
            List<MediaQuizDTO> randomQuizList = mediaQuizService.getSelectRandomInGroup(mediaGroupNo, quizNumber);
            model.addAttribute("randomQuizList", randomQuizList);

            List<String> answerList = new ArrayList<>();

            for (int i = 0; i< randomQuizList.size(); i++) {
                String answer = randomQuizList.get(i).getAnswer();
                answerList.add(answer);
            }

            model.addAttribute("mediaGroupDTO", mediaGroupDTO);
            model.addAttribute("randomQuizList", randomQuizList);
            model.addAttribute("answerList", answerList);
        } catch (IllegalStateException e) {
            return "header/userClosePopup";
        }

        return folderName + "test";
    }

    @RequestMapping("testProc")
    public String testProc(
            HttpSession httpSession,
            RedirectAttributes redirectAttrs,
            @ModelAttribute MediaQuizDTO mediaQuizDTO,
            @RequestParam("mediaGroupNo") int mediaGroupNo
    ) {
        int memberNo = sessionCheck(httpSession);
        if (memberNo < 0) {
            return "header/userClosePopup";
        }

        MediaCompletionDTO mediaCompletionDTO = mediaCompletionService.getSelectOneInMemberAndGroup(
                memberNo, mediaGroupNo
        );
        
        if (mediaCompletionDTO != null) {
            return "header/userClosePopup";
        }

        List<MediaQuizDTO> quizList = mediaQuizService.getSelectAllInNoList(mediaQuizDTO.getQuizNoList());
        List<List<String>> answerList = mediaQuizDTO.getAnswers();

        int correctCount = mediaQuizService.correctCountCalculator(quizList, answerList);
        boolean quizSuccess = false;

        if (correctCount >= correctNumber) {
            MemberDTO memberDTO = new MemberDTO();
            memberDTO.setNo(memberNo);

            MediaGroupDTO mediaGroupDTO = new MediaGroupDTO();
            mediaGroupDTO.setNo(mediaGroupNo);

            mediaCompletionDTO = new MediaCompletionDTO();
            mediaCompletionDTO.setMember(memberDTO);
            mediaCompletionDTO.setMediaGroup(mediaGroupDTO);

            mediaCompletionService.setInsert(mediaCompletionDTO);
            quizSuccess = true;
        }

        if (quizSuccess) {
            redirectAttrs.addFlashAttribute("result", "success");
        } else {
            redirectAttrs.addFlashAttribute("result", "fail");
        }

        return "redirect:/edu/media/test/" + mediaGroupNo;
    }

    // 서버 동영상 불러오기
    @RequestMapping("/video")
    public ResponseEntity<Resource> openVideo(
            @RequestParam String groupName,
            @RequestParam String fileName
    ) throws MalformedURLException, UnsupportedEncodingException {
        String filePath = "file:///C:/ForDog/Media/" + groupName + "/" + fileName;

        Resource videoResource = new UrlResource(filePath);

        if (!videoResource.exists() || !videoResource.isReadable()) {
            return ResponseEntity.notFound().build();
        }

        String encodedName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString()).replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .contentType(MediaTypeFactory.getMediaType(fileName)
                        .orElse(MediaType.APPLICATION_OCTET_STREAM))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename*=UTF-8''" + encodedName)
                .body(videoResource);
    }

    // 동영상 진행도
    @RequestMapping("/process")
    public ResponseEntity<?> process(
            @RequestBody MediaVideoProcessDTO mediaVideoProcessDTO,
            HttpSession httpSession
    ) throws Exception{
        int memberNo = (Integer) httpSession.getAttribute("memberNo");

        MemberDTO memberDTO = new MemberDTO();
        memberDTO.setNo(memberNo);
        mediaVideoProcessDTO.setMember(memberDTO);

        MediaVideoProcessDTO checkProcessDTO
                = mediaVideoProcessService.getSelectOneInMemberAndVideo(memberNo, mediaVideoProcessDTO.getMediaVideo().getNo());

        if (checkProcessDTO == null) {
            mediaVideoProcessService.setInsert(mediaVideoProcessDTO);
        } else {
            mediaVideoProcessDTO.setNo(checkProcessDTO.getNo());
            if (mediaVideoProcessDTO.getPlayTime() > checkProcessDTO.getPlayTime()) {
                mediaVideoProcessService.setUpdate(mediaVideoProcessDTO);
            }
        }

        // 동영상 이수율
        int mediaGroupNo = mediaVideoProcessDTO.getMediaVideo().getMediaGroup().getNo();
        List<MediaVideoDTO> videoList = mediaVideoService.getSelectAllInGroup(mediaGroupNo);
        List<MediaVideoProcessDTO> processList = mediaVideoProcessService.getSelectAllInMemberAndGroup(memberNo, mediaGroupNo);

        int completionRate = mediaVideoProcessService.completionRateCalculator(videoList, processList);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("completionRate", completionRate);

        return ResponseEntity.ok(response);
    }

    private int sessionCheck(HttpSession httpSession) {
        Object memberObject = httpSession.getAttribute("memberNo");
        if (memberObject == null) {
            return -1;
        }
        return (Integer) memberObject;
    }

}
