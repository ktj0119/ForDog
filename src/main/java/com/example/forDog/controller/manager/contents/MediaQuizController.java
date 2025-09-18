package com.example.forDog.controller.manager.contents;

import com.example.forDog.dto.MediaGroupDTO;
import com.example.forDog.dto.MediaQuizDTO;
import com.example.forDog.service.MediaGroupService;
import com.example.forDog.service.MediaQuizService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/manager/contents/quiz")
@RequiredArgsConstructor
public class MediaQuizController {

    private final MediaGroupService mgService;
    private final MediaQuizService service;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "searchType", required = false) String searchType,
                       @RequestParam(value = "searchKeyword", required = false) String searchKeyword
    ) {
        Page<MediaQuizDTO> pageList = service.getSelectAll(page, searchType, searchKeyword);
        List<MediaGroupDTO> groupList = mgService.getSelectAll();

        if(searchType == null) searchType = "";
        if(searchKeyword == null) searchKeyword = "";

        model.addAttribute("list", pageList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("paging", pageList);
        model.addAttribute("page", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        return "manager/contents/quiz/list";
    }

    @GetMapping("/view/{no}")
    public String view(Model model, MediaQuizDTO mediaQuizDTO) {
        MediaQuizDTO returnDTO = service.getSelectOne(mediaQuizDTO);
        model.addAttribute("returnDTO", returnDTO);
        return "manager/contents/quiz/view";
    }

    @GetMapping("/chuga")
    public String chuga(Model model, MediaGroupDTO mediaGroupDTO) {
        List<MediaGroupDTO> list = mgService.getSelectAll();
        model.addAttribute("list",list);
        return "manager/contents/quiz/chuga";
    }

    @GetMapping("/sujung/{no}")
    public String sujung(Model model, MediaQuizDTO mediaQuizDTO) {
        List<MediaGroupDTO> list = mgService.getSelectAll();
        model.addAttribute("list",list);
        MediaQuizDTO returnDTO = service.getSelectOne(mediaQuizDTO);
        model.addAttribute("returnDTO", returnDTO);
        return "manager/contents/quiz/sujung";
    }

    @RequestMapping("/chugaProc")
    public String chugaProc(MediaQuizDTO mediaQuizDTO) {
        service.setInsert(mediaQuizDTO);
        return "redirect:/manager/contents/quiz/list";
    }

    @RequestMapping("/sujungProc")
    public String sujungProc(MediaQuizDTO mediaQuizDTO) {
        service.setUpdate(mediaQuizDTO);
        return "redirect:/manager/contents/quiz/view/" + mediaQuizDTO.getNo();
    }

    @RequestMapping("/sakjeProc")
    public String sakjeProc(MediaQuizDTO mediaQuizDTO) {
        service.setDelete(mediaQuizDTO);
        return "redirect:/manager/contents/quiz/list";
    }
}
