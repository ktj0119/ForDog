package com.example.forDog.controller.manager.homepage;

import com.example.forDog.dto.NoticeDTO;
import com.example.forDog.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/manager/homepage/notice")
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService service;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "searchType", required = false) String searchType,
                       @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {
        Page<NoticeDTO> pageList = service.getSelectAll(page, searchType, searchKeyword);

        if(searchType == null) searchType = "";
        if(searchKeyword == null) searchKeyword = "";

        model.addAttribute("list", pageList);
        model.addAttribute("paging", pageList);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        return "manager/homepage/notice/list";
    }

    @GetMapping("/view/{no}")
    public String view(Model model, NoticeDTO noticeDTO) {
        NoticeDTO returnDTO = service.getSelectOne(noticeDTO);
        model.addAttribute("returnDTO",returnDTO);
        return "manager/homepage/notice/view";
    }

    @GetMapping("/chuga")
    public String chuga() {
        return "manager/homepage/notice/chuga";
    }

    @GetMapping("/sujung/{no}")
    public String sujung(Model model, NoticeDTO noticeDTO) {
        NoticeDTO returnDTO = service.getSelectOne(noticeDTO);
        model.addAttribute("returnDTO",returnDTO);
        return "manager/homepage/notice/sujung";
    }

    @RequestMapping("/chugaProc")
    public String chugaProc(NoticeDTO noticeDTO) {
        service.setInsert(noticeDTO);
        return "redirect:/manager/homepage/notice/list";
    }

    @RequestMapping("/sujungProc")
    public String sujungProc(NoticeDTO noticeDTO) {
        service.setUpdate(noticeDTO);
        return "redirect:/manager/homepage/notice/view/" + noticeDTO.getNo();
    }

    @RequestMapping("/sakjeProc")
    public String sakjeProc(NoticeDTO noticeDTO) {
        service.setDelete(noticeDTO);
        return "redirect:/manager/homepage/notice/list";
    }
}
