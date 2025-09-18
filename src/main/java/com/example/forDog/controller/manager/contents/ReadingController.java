package com.example.forDog.controller.manager.contents;

import com.example.forDog.dto.ReadingDTO;
import com.example.forDog.service.ReadingService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Controller
@RequestMapping("/manager/contents/reading")
@RequiredArgsConstructor
public class ReadingController {

    private final ReadingService service;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "searchType", required = false) String searchType,
                       @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

        Page<ReadingDTO> pageList = service.getSelectAll(page, searchType, searchKeyword);
        List<String> groupList = service.getReadingGroup();

        for(int i = 0; i < groupList.size(); i++) {
            System.out.println(groupList.get(i));
        }

        if(searchType == null) searchType = "";
        if(searchKeyword == null) searchKeyword = "";

        model.addAttribute("list", pageList);
        model.addAttribute("totalCount", pageList.getTotalElements());

        model.addAttribute("groupList", groupList);

        model.addAttribute("paging", pageList);
        model.addAttribute("page", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        return "manager/contents/reading/list";
    }

    @GetMapping("/view/{no}")
    public String view(Model model, ReadingDTO readingDTO) {
        ReadingDTO returnDTO = service.getSelectOne(readingDTO);
        model.addAttribute("returnDTO", returnDTO);
        return "manager/contents/reading/view";
    }

    // 0815 파일 다운로드 추가(TJ)
    @GetMapping("/{no}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable int no) throws MalformedURLException {
        ReadingDTO dto = service.getSelectOne(new ReadingDTO() {{ setNo(no); }});
        if (dto == null || dto.getAttachment() == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = service.loadFileAsResource(dto.getAttachment());

        String originalFileName = dto.getAttachment();
        System.out.println(originalFileName);
        String displayFileName = originalFileName;
        System.out.println(displayFileName);
        int underscoreIndex = originalFileName.indexOf('_');
        if (underscoreIndex != -1 && underscoreIndex < originalFileName.length() - 1) {
            displayFileName = originalFileName.substring(underscoreIndex + 1);
        }
        System.out.println(displayFileName);

        String encodedFileName = URLEncoder.encode(displayFileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"); // 공백을 + 대신 %20으로

        String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    @GetMapping("/chuga")
    public String chuga(Model model, ReadingDTO readingDTO) {
        model.addAttribute("readingDTO",readingDTO);
        return "manager/contents/reading/chuga";
    }

    @GetMapping("/sujung/{no}")
    public String sujung(Model model, ReadingDTO readingDTO) {
        ReadingDTO returnDTO = service.getSelectOne(readingDTO);
        model.addAttribute("returnDTO", returnDTO);
        return "manager/contents/reading/sujung";
    }

    @RequestMapping("/chugaProc")
    public String chugaProc(ReadingDTO readingDTO) {
        service.setInsert(readingDTO);
        return "redirect:/manager/contents/reading/list";
    }

    @RequestMapping("/sujungProc")
    public String sujungProc(ReadingDTO readingDTO) {
        service.setUpdate(readingDTO);
        return "redirect:/manager/contents/reading/view/" + readingDTO.getNo();
    }

    @RequestMapping("/sakjeProc")
    public String sakjeProc(ReadingDTO readingDTO) {
        service.setDelete(readingDTO);
        return "redirect:/manager/contents/reading/list";
    }
}
