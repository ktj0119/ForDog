package com.example.forDog.controller.user;

import com.example.forDog.dto.GameDataDto;
import com.example.forDog.service.MemberService;
import com.fasterxml.jackson.databind.ObjectMapper; // ObjectMapper를 주입을 위해 임포트
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/game")
@RequiredArgsConstructor
public class GameApiController {

    private final MemberService memberService;
    private final ObjectMapper objectMapper; // ObjectMapper를 주입

    // 모든 게임 데이터 불러오기
    @GetMapping("/load/all/{userId}")
    public ResponseEntity<?> loadAllGameData(@PathVariable String userId) {
        try {
            // 서비스에서 데이터 객체 컬렉션 받기
            Collection<Map<String, Object>> gameDataList = memberService.getAllGameData(userId);

            // JSON 배열 "[{...},{...}]" 형태로 변환하여 응답
            return new ResponseEntity<>(gameDataList, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("[]", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게임 데이터 저장 API
    @PutMapping("/save")
    public ResponseEntity<String> saveGameData(@RequestBody GameDataDto gameData) {
        try {
            String userId = gameData.getUserId();

            // DTO 객체를 JSON 문자열로 변환
            String gameDataJson = objectMapper.writeValueAsString(gameData);

            // 위에서 변환된 JSON 문자열을 데이터베이스에 저장
            memberService.saveGameData(userId, gameDataJson);

            return new ResponseEntity<>("Game data saved successfully.", HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Failed to save game data.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    // 게임 데이터 불러오기 API
    @GetMapping("/load/{userId}")
    public ResponseEntity<String> loadGameData(@PathVariable String userId, @RequestParam int slot) {
        try {
            String gameData = memberService.getGameData(userId, slot);
            return new ResponseEntity<>(gameData, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // 에러 로그 출력
            // 에러 발생 시 유니티가 파싱할 수 있도록 빈 JSON 객체 반환
            return new ResponseEntity<>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // 게임 데이터 삭제 API
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<String> deleteGameData(@PathVariable String userId, @RequestParam int slot) {
        try {
            memberService.deleteGameData(userId, slot);
            return new ResponseEntity<>("Game data for slot " + slot + " deleted successfully.", HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace(); // 에러 로그 출력
            return new ResponseEntity<>("Failed to delete game data.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/load/latest/{userId}")
    public ResponseEntity<String> loadLatestGameData(@PathVariable String userId) {
        try {
            String latestGameData = memberService.getLatestGameData(userId);
            return new ResponseEntity<>(latestGameData, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("{}", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}