package com.example.forDog.controller.user;

import com.example.forDog.dto.NoticeDTO;
import com.example.forDog.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("news/")
@RequiredArgsConstructor
public class NewsController {

    private final NoticeService noticeService;
    private String folderName = "user/news/";

    @RequestMapping("faq")
    public String faq(
            Model model
    ) {
        model.addAttribute("currentPage", "faq");

        return folderName + "faq";
    }

    @RequestMapping("notice")
    public String notice(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword
    ) {
        Page<NoticeDTO> pageList = noticeService.getSelectAll(page, searchKeyword);

        if(searchType == null) searchType = "";
        if(searchKeyword == null) searchKeyword = "";

        model.addAttribute("paging", pageList);
        model.addAttribute("totalCount", pageList.getTotalElements());

        model.addAttribute("page", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);

        model.addAttribute("currentPage", "notice");

        return folderName + "notice";
    }

    @RequestMapping("notice/{noticeNo}")
    public String noticeView(
            Model model,
            @PathVariable("noticeNo") int noticeNo,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword
    ) {
        NoticeDTO returnDTO = noticeService.getSelectOne(noticeNo);

        NoticeDTO prevPost = noticeService.getSelectPrev(noticeNo, searchKeyword);
        NoticeDTO nextPost = noticeService.getSelectNext(noticeNo, searchKeyword);

        noticeService.updateCount(noticeNo);

        returnDTO.setContent(returnDTO.getContent().replace("\n", "<br>"));

        if(searchType == null) searchType = "";
        if(searchKeyword == null) searchKeyword = "";

        model.addAttribute("returnDTO", returnDTO);

        model.addAttribute("prevPost", prevPost);
        model.addAttribute("nextPost", nextPost);

        model.addAttribute("page", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);

        model.addAttribute("currentPage", "notice");

        return folderName + "noticeView";
    }

}
