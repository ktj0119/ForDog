package com.example.forDog.controller.user;

import com.example.forDog.dto.ReadingDTO;
import com.example.forDog.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("edu/reading/")
@RequiredArgsConstructor
public class EduReadingController {

    private final ReadingService readingService;
    private String folderName = "user/edu/reading/";

    @RequestMapping("list")
    public String list(
            Model model,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword
    ) {
        Page<ReadingDTO> pageList = readingService.getSelectAll(page, searchType, searchKeyword);
        List<String> groupList = readingService.getReadingGroup();

        if(searchType == null) searchType = "";
        if(searchKeyword == null) searchKeyword = "";

        model.addAttribute("paging", pageList);
        model.addAttribute("totalCount", pageList.getTotalElements());

        model.addAttribute("groupList", groupList);

        model.addAttribute("page", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);

        model.addAttribute("currentPage", "reading");

        return folderName + "list";
    }

    @RequestMapping("view/{no}")
    public String view(
            Model model,
            ReadingDTO readingDTO,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "searchType", required = false) String searchType,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword
    ) {
        ReadingDTO returnDTO = readingService.getSelectOne(readingDTO);

        ReadingDTO prevPost = readingService.getSelectPrev(readingDTO.getNo(), searchType, searchKeyword);
        ReadingDTO nextPost = readingService.getSelectNext(readingDTO.getNo(), searchType, searchKeyword);

        String fileName = "";

        if (returnDTO.getAttachment() != null) {
            fileName = readingService.getFileName(returnDTO.getAttachment());
        }

        returnDTO.setContent(returnDTO.getContent().replace("\n", "<br>"));

        if(searchType == null) searchType = "";
        if(searchKeyword == null) searchKeyword = "";

        model.addAttribute("returnDTO", returnDTO);
        model.addAttribute("fileName", fileName);

        model.addAttribute("prevPost", prevPost);
        model.addAttribute("nextPost", nextPost);

        model.addAttribute("page", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);

        model.addAttribute("currentPage", "reading");

        return folderName + "view";
    }

    @RequestMapping("/download")
    public ResponseEntity<Resource> downloadFile(
            @RequestParam String fileName
    ) throws IOException {
        Resource file = readingService.loadFile(fileName);
        String encodeFileName = readingService.encodeFile(fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header("Content-Disposition", "attachment; filename*=UTF-8''" + encodeFileName)
                .body(file);
    }

}
