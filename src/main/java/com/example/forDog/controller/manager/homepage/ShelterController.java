package com.example.forDog.controller.manager.homepage;

import com.example.forDog.dto.ShelterDTO;
import com.example.forDog.service.ShelterService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;

@Controller
@RequestMapping("/manager/homepage/shelter")
@RequiredArgsConstructor
public class ShelterController {

    private final ShelterService service;

    @GetMapping("/list")
    public String list(Model model,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "searchType", required = false) String searchType,
                       @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {
        // 페이징된 데이터 가져오기
        Page<ShelterDTO> pageList = service.getSelectAll(page, searchType, searchKeyword);
        List<ShelterDTO> list = pageList.getContent();

        // searchType과 searchKeyword가 null일 경우 빈 값으로 설정
        if (searchType == null) searchType = "";
        if (searchKeyword == null) searchKeyword = "";

        // 전화번호와 주소에 대한 처리
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).getPhone().equals("***********"))
                list.get(i).setPhone("개별 문의");

            if (list.get(i).getAddress() != null && list.get(i).getAddress().contains("null"))
                list.get(i).setAddress(list.get(i).getAddress().replace("null", ""));
        }

        // 모델에 pageList 추가 (content를 사용하려면 Page 객체를 그대로 넘겨야 함)
        model.addAttribute("list", pageList);
        model.addAttribute("paging", pageList);
        model.addAttribute("searchType", searchType);
        model.addAttribute("searchKeyword", searchKeyword);

        return "manager/homepage/shelter/list";
    }

    @GetMapping("/view/{no}")
    public String view(Model model, ShelterDTO shelterDTO) {
        ShelterDTO returnDTO = service.getSelectOne(shelterDTO);
        model.addAttribute("returnDTO", returnDTO);
        return "manager/homepage/shelter/view";
    }

    @GetMapping("/chuga")
    public String chuga() {
        return "manager/homepage/shelter/chuga";
    }

    @GetMapping("/sujung/{no}")
    public String sujung(Model model, ShelterDTO shelterDTO) {
        ShelterDTO returnDTO = service.getSelectOne(shelterDTO);
        model.addAttribute("returnDTO", returnDTO);
        return "manager/homepage/shelter/sujung";
    }

    // 주소 api 추가, 관할구역 select화 해서 null 체크(TJ)
    @RequestMapping("/chugaProc")
    public String chugaProc(ShelterDTO shelterDTO, @RequestParam("addressMain") String addressMain, @RequestParam("addressDetail") String addressDetail) {
        if (addressDetail == null) addressDetail = "";
        System.out.println("주소 : " + addressMain + "///" + addressDetail);
        String address = addressMain + " " + addressDetail;
        System.out.println("최종주소 : " + address);
        shelterDTO.setAddress(address);
        service.setInsert(shelterDTO);
        return "redirect:/manager/homepage/shelter/list";
    }

    // 주소 api 추가, 관할구역 select화 해서 null 체크(TJ)
    @RequestMapping("/sujungProc")
    public String sujungProc(ShelterDTO shelterDTO, @RequestParam("addressMain") String addressMain, @RequestParam("addressDetail") String addressDetail) {
        if (addressDetail == null) addressDetail = "";
        System.out.println("주소 : " + addressMain + "///" + addressDetail);
        String address = addressMain + " " + addressDetail;
        System.out.println("최종주소 : " + address);
        shelterDTO.setAddress(address);
        service.setUpdate(shelterDTO);
        return "redirect:/manager/homepage/shelter/view/" + shelterDTO.getNo();
    }

    @RequestMapping("/sakjeProc")
    public String sakjeProc(ShelterDTO shelterDTO) {
        service.setDelete(shelterDTO);
        return "redirect:/manager/homepage/shelter/list";
    }

    // CSV 업로드 처리
    @PostMapping("/upload")
    public String uploadShelterCsv(@RequestParam("file") MultipartFile file) {
        try {
            service.uploadCsv(file);
        } catch (Exception e) {
            e.printStackTrace(); // 적절한 로깅 추천
        }
        return "redirect:/manager/homepage/shelter/list?uploaded=true";
    }

    // CSV 다운로드
    @GetMapping("/download")
    public ResponseEntity<InputStreamResource> downloadShelterCsv() {
        ByteArrayInputStream csv = service.generateShelterCsv();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=shelters.csv");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(new InputStreamResource(csv));
    }
}
