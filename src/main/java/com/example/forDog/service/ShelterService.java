package com.example.forDog.service;

import com.example.forDog.dto.ShelterDTO;
import com.example.forDog.entity.Shelter;
import com.example.forDog.repository.ShelterRepository;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShelterService {

    private final ShelterRepository repository;
    private final ModelMapper mapper;

    public List<ShelterDTO> getSelectAll() {
        List<Shelter> entityList = repository.findAll();
        List<ShelterDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), ShelterDTO.class));
        }
        return dtoList;
    }

    public Page<ShelterDTO> getSelectAll(int page, String searchType, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, 20, Sort.by(Sort.Order.asc("no")));
        Page<Shelter> pageList;

        boolean existType = searchType != null && !searchType.isEmpty();
        boolean existKeyword = searchKeyword != null && !searchKeyword.isEmpty();

        if (!existType && !existKeyword) {
            pageList = repository.findAll(pageable);
        } else if (existType && !existKeyword) {
            pageList = repository.findByRegionContaining(searchType, pageable);
        } else if (!existType && existKeyword) {
            pageList = repository.findByShelterNameContaining(searchKeyword, pageable);
        } else {
            pageList = repository.findByRegionContainingAndShelterNameContaining(searchType, searchKeyword, pageable);
        }

        return pageList.map(shelter -> mapper.map(shelter, ShelterDTO.class));
    }

    public ShelterDTO getSelectOne(ShelterDTO shelterDTO) {
        Optional<Shelter> os = repository.findById(shelterDTO.getNo());

        if (os.isEmpty()) {
            return null;
        }

        Shelter shelter = os.get();
        return mapper.map(shelter, ShelterDTO.class);
    }

    public void setInsert(ShelterDTO shelterDTO) {
        repository.save(mapper.map(shelterDTO, Shelter.class));
    }

    public void setUpdate(ShelterDTO shelterDTO) {
        repository.save(mapper.map(shelterDTO, Shelter.class));
    }

    public void setDelete(ShelterDTO shelterDTO) {
        repository.delete(mapper.map(shelterDTO, Shelter.class));
    }

    // CSV 업로드 처리
    public void uploadCsv(MultipartFile file) throws IOException, CsvValidationException {
        List<Shelter> sheltersToSave = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String[] line;
            boolean isFirst = true;

            while ((line = reader.readNext()) != null) {
                if (isFirst) {
                    // 첫 줄에서 BOM 제거
                    line[0] = line[0].replace("\uFEFF", "");
                    isFirst = false;
                    continue; // 헤더는 건너뜀
                }

                int no = Integer.parseInt(line[0].trim());
                String region = line[1].trim();
                String shelterName = line[2].trim();
                String phone = line[3].trim();
                String address = line[4].trim();

                Shelter shelter;
                if (repository.existsById(no)) {
                    shelter = repository.findById(no).get();
                } else {
                    shelter = new Shelter();
                }

                shelter.setRegion(region);
                shelter.setShelterName(shelterName);
                shelter.setPhone(phone);
                shelter.setAddress(address);

                sheltersToSave.add(shelter);
            }
        }

        repository.saveAll(sheltersToSave);
    }

    // CSV 다운로드 생성
    public ByteArrayInputStream generateShelterCsv() {
        List<ShelterDTO> shelters = getSelectAll();

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        // UTF-8 BOM 추가 (Excel 호환)
        out.write(0xEF);
        out.write(0xBB);
        out.write(0xBF);

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(out, StandardCharsets.UTF_8));

        writer.println("no,region,shelterName,phone,address"); // 헤더

        for (ShelterDTO s : shelters) {
            writer.printf("%d,%s,%s,%s,%s%n",
                    s.getNo(), s.getRegion(), s.getShelterName(), s.getPhone(), s.getAddress());
        }

        writer.flush();
        return new ByteArrayInputStream(out.toByteArray());
    }

    public long countShelter() {
        return repository.count();
    }

}
