package com.example.forDog.controller.user;

import com.example.forDog.dto.ShelterDTO;
import com.example.forDog.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("guide/")
@RequiredArgsConstructor
public class GuideController {

    private final ShelterService shelterService;
    private String folderName = "user/guide/";

    @RequestMapping("petRegister")
    public String petRegister(
            Model model
    ) {
        model.addAttribute("currentPage", "petRegister");

        return folderName + "petRegister";
    }

    @RequestMapping("shelter")
    public String shelter(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword
    ) {
        Page<ShelterDTO> pageList = shelterService.getSelectAll(page, searchType, searchKeyword);
        List<ShelterDTO> list = pageList.getContent();

        if(searchType == null) searchType = "";
        if(searchKeyword == null) searchKeyword = "";

        // 전화번호가 *** 이면 개별 문의로, 주소에 'null' 단어가 포함되지 않게 설정
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPhone().equals("***********"))
                list.get(i).setPhone("개별 문의");

            if (list.get(i).getAddress() != null && list.get(i).getAddress().contains("null"))
                list.get(i).setAddress(list.get(i).getAddress().replace("null", ""));
        }

        model.addAttribute("list", list);
        model.addAttribute("paging", pageList);

        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);

        model.addAttribute("currentPage", "shelter");

        return folderName + "shelter";
    }

    @RequestMapping("volunteer")
    public String volunteer(
            Model model
    ) {
        model.addAttribute("currentPage", "volunteer");

        return folderName + "volunteer";
    }

}
