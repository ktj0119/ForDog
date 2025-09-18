package com.example.forDog.controller.manager.contents;

import com.example.forDog.dto.MediaGroupDTO;
import com.example.forDog.dto.MediaVideoDTO;
import com.example.forDog.service.MediaGroupService;
import com.example.forDog.service.MediaVideoService;
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
@RequestMapping("/manager/contents/media")
@RequiredArgsConstructor
public class MediaVideoController {

    private final MediaGroupService mgService;
    private final MediaVideoService service;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "searchType", required = false) String searchType,
                       @RequestParam(value = "searchKeyword", required = false) String searchKeyword
    ) {

        Page<MediaVideoDTO> pageList = service.getSelectAll(page, searchType, searchKeyword);
        List<MediaGroupDTO> groupList = mgService.getSelectAll();

        if(searchType == null) searchType = "";
        if(searchKeyword == null) searchKeyword = "";

        model.addAttribute("list", pageList);
        model.addAttribute("groupList", groupList);
        model.addAttribute("paging", pageList);
        model.addAttribute("page", page);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);
        return "manager/contents/media/list";
    }

    @GetMapping("/view/{no}")
    public String view(Model model, MediaVideoDTO mediaVideoDTO) {
        MediaVideoDTO returnDTO = service.getSelectOne(mediaVideoDTO);
        model.addAttribute("returnDTO", returnDTO);
        return "manager/contents/media/view";
    }

    // 0818 파일 다운로드 추가(TJ)
    @GetMapping("/{no}/download")
    public ResponseEntity<Resource> downloadFile(@PathVariable int no) throws MalformedURLException {
        MediaVideoDTO dto = service.getSelectOne(new MediaVideoDTO() {{ setNo(no); }});
        if (dto == null || dto.getUrl() == null) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = service.loadFileAsResource(dto.getMediaGroup().getName() + "/" +dto.getUrl());

        String originalFileName = dto.getUrl();
        String displayFileName = originalFileName;
        int underscoreIndex = originalFileName.indexOf('_');
        if (underscoreIndex != -1 && underscoreIndex < originalFileName.length() - 1) {
            displayFileName = originalFileName.substring(underscoreIndex + 1);
        }

        String encodedFileName = URLEncoder.encode(displayFileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20"); // 공백을 + 대신 %20으로

        String contentDisposition = "attachment; filename*=UTF-8''" + encodedFileName;

        return ResponseEntity.ok()
                .contentType(MediaType.valueOf("video/mp4"))
                .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
                .body(resource);
    }

    @GetMapping("/chuga")
    public String chuga(Model model, MediaVideoDTO mediaVideoDTO) {
        List<MediaGroupDTO> list = mgService.getSelectAll();
        model.addAttribute("list", list);
        model.addAttribute("mediaVideoDTO",mediaVideoDTO);
        return "manager/contents/media/chuga";
    }

    @GetMapping("/sujung/{no}")
    public String sujung(Model model, MediaVideoDTO mediaVideoDTO) {
        List<MediaGroupDTO> list = mgService.getSelectAll();
        model.addAttribute("list", list);
        MediaVideoDTO returnDTO = service.getSelectOne(mediaVideoDTO);
        model.addAttribute("returnDTO", returnDTO);
        return "manager/contents/media/sujung";
    }

    @RequestMapping("/chugaProc")
    public String chugaProc(MediaVideoDTO mediaVideoDTO) {
        service.setInsert(mediaVideoDTO);
        return "redirect:/manager/contents/media/list";
    }

    @RequestMapping("/sujungProc")
    public String sujungProc(MediaVideoDTO mediaVideoDTO) {
        service.setUpdate(mediaVideoDTO);
        return "redirect:/manager/contents/media/view/" + mediaVideoDTO.getNo();
    }

    @RequestMapping("/sakjeProc")
    public String sakjeProc(MediaVideoDTO mediaVideoDTO) {
        service.setDelete(mediaVideoDTO);
        return "redirect:/manager/contents/media/list";
    }
}
