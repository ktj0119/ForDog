package com.example.forDog.service;

import com.example.forDog.dto.GameDataDto;
import com.example.forDog.dto.MemberDTO;
import com.example.forDog.entity.Member;
import com.example.forDog.entity.Shelter;
import com.example.forDog.repository.MemberRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository repository;
    private final ModelMapper mapper;
    private final ObjectMapper objectMapper;

    public List<MemberDTO> getSelectAll() {
        List<Member> entityList = repository.findAll();
        List<MemberDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), MemberDTO.class));
        }

        return dtoList;
    }

    public Page<MemberDTO> getSelectAll(int page, String searchType, String keyword) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("no")));
        Page<Member> pageList;

        boolean noKeyword = (searchType == null || searchType.isEmpty()) &&
                (keyword == null || keyword.isEmpty());
        boolean noSearch = (searchType == null || searchType.isEmpty()) &&
                (keyword != null);

        if (noKeyword) {
            pageList = repository.findAll(pageable);
        } else if (noSearch) {
            pageList = repository.findByIdContainingOrNameContainingOrPhoneContaining(
                    keyword, keyword, keyword, pageable);
        } else {
            switch (searchType) {
                case "id":
                    pageList = repository.findByIdContaining(keyword, pageable);
                    break;
                case "name":
                    pageList = repository.findByNameContaining(keyword, pageable);
                    break;
                case "phone":
                    pageList = repository.findByPhoneContaining(keyword, pageable);
                    break;
                default:
                    pageList = repository.findAll(pageable);
            }
        }

        return pageList.map(member -> mapper.map(member, MemberDTO.class));
    }

    public MemberDTO getSelectOne(MemberDTO memberDTO) {
        Optional<Member> om = repository.findById(memberDTO.getNo());

        if (om.isEmpty()) {
            return null;
        }

        Member member = om.get();
        return mapper.map(member, MemberDTO.class);
    }

    public MemberDTO getSelectOne(int no) {
        Optional<Member> om = repository.findById(no);

        if (om.isEmpty()) {
            return null;
        }

        Member member = om.get();
        return mapper.map(member, MemberDTO.class);
    }

    public MemberDTO getSelectOne(String id) {
        Optional<Member> om = repository.findById(id);

        if (om.isEmpty()) {
            return null;
        }

        Member member = om.get();
        return mapper.map(member, MemberDTO.class);
    }

    public boolean getSelectId(String id) {
        return repository.existsById(id);
    }

    public void setInsert(MemberDTO memberDTO) {
        repository.save(mapper.map(memberDTO, Member.class));
    }

    public void setUpdate(MemberDTO memberDTO) {
        repository.save(mapper.map(memberDTO, Member.class));
    }

    public void setDelete(MemberDTO memberDTO) {
        repository.delete(mapper.map(memberDTO, Member.class));
    }

    // 게임 진행도 - 슬롯 별 제일 높은 Scene 기준으로 반환 + 전체 씬 목록 반환
    public Map<String, Object> getGameSaveDataProcess(int no) {
        MemberDTO memberDTO = getSelectOne(no);
        String gameSaveData = memberDTO.getGameSaveData();

        Map<String, Object> topValue = new HashMap<>();
        List<String> sceneIndexName
                = new ArrayList<>(Arrays.asList("City1", "City2", "City3", "EndingScene"));

        if (gameSaveData == null || gameSaveData.isEmpty()) {
            topValue.put("topSceneName", null);
            topValue.put("topSceneIndex", 0);
            topValue.put("sceneIndexName", sceneIndexName);
            return topValue;
        }

        int slots = 3;
        List<String> gameSaveDataSceneList = new ArrayList<>();

        for (int i = 1; i <= slots; i++) {
            String slot = "\"" + i + "\":";
            // 슬롯이 존재하면
            if (gameSaveData.contains(slot)) {
                // 슬롯 별 currentSceneName의 시작 지점을 계산
                int index = gameSaveData.indexOf("\"currentSceneName\"", gameSaveData.indexOf(slot));
                // 씬 이름이 존재 하면, currentSceneName: 다음에 등장하는 " 문자열부터 " 있는 곳까지 계산
                if (index != -1) {
                    int start = gameSaveData.indexOf("\"", index + 18) + 1;
                    int end = gameSaveData.indexOf("\"", start);
                    String sceneName = gameSaveData.substring(start, end);
                    gameSaveDataSceneList.add(sceneName);
                } else {
                    gameSaveDataSceneList.add(null);
                }
            } else {
                // 슬롯 자체가 없음
                gameSaveDataSceneList.add(null);
            }
        }

        String topSceneName = "";
        int topSceneIndex = 0;

        for (String sceneName : gameSaveDataSceneList) {
            if (sceneName != null && sceneIndexName.contains(sceneName)) {
                int index = sceneIndexName.indexOf(sceneName);
                if (index > topSceneIndex) {
                    topSceneIndex = index;
                    topSceneName = sceneName;
                }
            }
        }
        topSceneIndex = topSceneIndex + 1;

        topValue.put("topSceneName", topSceneName);
        topValue.put("topSceneIndex", topSceneIndex);
        topValue.put("sceneIndexName", sceneIndexName);

        return topValue;
    }

    // [다희] 수정 시작 부분
    // 전체 슬롯 데이터를 불러오기
    @Transactional(readOnly = true)
    public Collection<Map<String, Object>> getAllGameData(String userId) throws Exception {
        Member member = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        String allSlotsJson = member.getGameSaveData();

        if (allSlotsJson == null || allSlotsJson.isEmpty()) {
            // 데이터가 없으면 빈 컬렉션을 반환
            return new ArrayList<>();
        }

        // JSON 문자열을 Map<String, Map<String, Object>> 형태로 변환
        Map<String, Map<String, Object>> allSlotsMap = objectMapper.readValue(allSlotsJson, new TypeReference<>() {});

        // Map의 value들(각 슬롯의 데이터 맵)만 그대로 반환
        return allSlotsMap.values();
    }

    // 특정 슬롯 데이터를 불러오기
    @Transactional(readOnly = true)
    public String getGameData(String userId, int slot) throws Exception {
        Member member = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        String allSlotsJson = member.getGameSaveData();
        Map<String, Object> slotDataMap = null;

        if (allSlotsJson != null && !allSlotsJson.isEmpty()) {
            Map<String, Map<String, Object>> allSlotsMap = objectMapper.readValue(allSlotsJson, new TypeReference<>() {});
            slotDataMap = allSlotsMap.get(String.valueOf(slot));
        }

        if (slotDataMap != null) {
            return objectMapper.writeValueAsString(slotDataMap);
        }
        else {
            // 해당 슬롯 데이터가 없으면, 빈 객체에 슬롯 번호만 담아 반환
            GameDataDto emptySlotDto = new GameDataDto();
            emptySlotDto.setSaveSlot(slot);
            return objectMapper.writeValueAsString(emptySlotDto);
        }
    }

    // 특정 슬롯에 게임 데이터 저장하기
    public void saveGameData(String userId, String newGameDataJson) throws Exception {
        Member member = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        Map<String, Object> newGameDataMap = objectMapper.readValue(newGameDataJson, new TypeReference<>() {});
        String slotToSave = newGameDataMap.get("saveSlot").toString();

        String allSlotsJson = member.getGameSaveData();
        Map<String, Map<String, Object>> allSlotsMap;

        if (allSlotsJson == null || allSlotsJson.isEmpty()) {
            allSlotsMap = new HashMap<>();
        } else {
            allSlotsMap = objectMapper.readValue(allSlotsJson, new TypeReference<>() {});
        }

        allSlotsMap.put(slotToSave, newGameDataMap);

        String updatedAllSlotsJson = objectMapper.writeValueAsString(allSlotsMap);
        member.setGameSaveData(updatedAllSlotsJson);
    }

    // 특정 슬롯의 데이터 삭제하기
    public void deleteGameData(String userId, int slot) throws Exception {
        Member member = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        String allSlotsJson = member.getGameSaveData();
        if (allSlotsJson == null || allSlotsJson.isEmpty()) {
            return;
        }

        Map<String, Object> allSlotsMap = objectMapper.readValue(allSlotsJson, new TypeReference<>() {});

        allSlotsMap.remove(String.valueOf(slot));

        String updatedAllSlotsJson = objectMapper.writeValueAsString(allSlotsMap);
        member.setGameSaveData(updatedAllSlotsJson);
    }

    public String getLatestGameData(String userId) throws Exception {
        Member member = repository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다. ID: " + userId));

        String allSlotsJson = member.getGameSaveData();
        if (allSlotsJson == null || allSlotsJson.isEmpty() || allSlotsJson.equals("{}")) {
            return "{}";
        }

        Map<String, Map<String, Object>> allSlotsMap = objectMapper.readValue(allSlotsJson, new TypeReference<>() {});

        Optional<Map<String, Object>> latestSlotData = allSlotsMap.values().stream()
                .filter(slotData -> slotData != null && slotData.containsKey("saveTime") && slotData.get("saveTime") != null)
                .max(Comparator.comparing(slotData ->
                        LocalDateTime.parse(slotData.get("saveTime").toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                ));

        if (latestSlotData.isPresent()) {
            return objectMapper.writeValueAsString(latestSlotData.get());
        }

        return "{}";
    }
    // [다희] 수정 종료 부분

    public long countMember() {
        return repository.count();
    }

}
