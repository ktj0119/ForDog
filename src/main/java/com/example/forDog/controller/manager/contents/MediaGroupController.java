package com.example.forDog.controller.manager.contents;

import com.example.forDog.dto.MediaGroupDTO;
import com.example.forDog.service.MediaGroupService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/manager/contents/mediaGroup")
@RequiredArgsConstructor
public class MediaGroupController {

    private final MediaGroupService service;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

        Page<MediaGroupDTO> pageList = service.getSelectAll(page, searchKeyword);

        Page<MediaGroupDTO> mediaGroups = service.getSelectFilterAll(10, page, searchKeyword);


        if(searchKeyword == null) searchKeyword = "";

        model.addAttribute("list", pageList);
        model.addAttribute("paging", pageList);
        model.addAttribute("searchKeyword", searchKeyword);
        return "manager/contents/mediaGroup/list";
    }

    @GetMapping("/view/{no}")
    public String view(MediaGroupDTO mediaGroupDTO, Model model) {
        MediaGroupDTO returnDTO = service.getSelectOne(mediaGroupDTO);
        model.addAttribute("returnDTO",returnDTO);
        return "manager/contents/mediaGroup/view";
    }

    @GetMapping("/chuga")
    public String chuga() {
        return "manager/contents/mediaGroup/chuga";
    }

    @GetMapping("/sujung/{no}")
    public String sujung(MediaGroupDTO mediaGroupDTO, Model model) {
        MediaGroupDTO returnDTO = service.getSelectOne(mediaGroupDTO);
        model.addAttribute("returnDTO",returnDTO);
        return "manager/contents/mediaGroup/sujung";
    }

    @RequestMapping("/chugaProc")
    public String chugaProc(MediaGroupDTO mediaGroupDTO) {
        mediaGroupDTO.setIsActive(false);
        service.setInsert(mediaGroupDTO);
        return "redirect:/manager/contents/mediaGroup/list";
    }

    @RequestMapping("/sujungProc")
    public String sujungProc(MediaGroupDTO mediaGroupDTO) {
        service.setUpdate(mediaGroupDTO);
        return "redirect:/manager/contents/mediaGroup/view/" + mediaGroupDTO.getNo();
    }

    @RequestMapping("/sakjeProc")
    public String sakjeProc(MediaGroupDTO mediaGroupDTO) {
        service.setDelete(mediaGroupDTO);
        return "redirect:/manager/contents/mediaGroup/list";
    }
}
