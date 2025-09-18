package com.example.forDog.service;


import com.example.forDog.dto.MediaGroupDTO;
import com.example.forDog.dto.MediaQuizDTO;
import com.example.forDog.entity.MediaGroup;
import com.example.forDog.entity.MediaQuiz;
import com.example.forDog.repository.MediaGroupRepository;
import com.example.forDog.repository.MediaQuizRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MediaQuizService {

    private final MediaQuizRepository repository;

    private final MediaGroupRepository mgRepository;

    private final ModelMapper mapper;

    public List<MediaQuizDTO> getSelectAll() {
        List<MediaQuiz> entityList = repository.findAll();
        List<MediaQuizDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), MediaQuizDTO.class));
        }
        return dtoList;
    }

    public Page<MediaQuizDTO> getSelectAll(int page, String searchType, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.asc("no")));
        Page<MediaQuiz> pageList;

        boolean existType = searchType != null && !searchType.isEmpty();
        boolean existKeyword = searchKeyword != null && !searchKeyword.isEmpty();

        Integer groupNo = null;
        if (existType) {
            try {
                groupNo = Integer.parseInt(searchType);
            } catch (NumberFormatException e) {
                groupNo = null;
            }
        }

        if (groupNo == null && !existKeyword) {
            pageList = repository.findAll(pageable);
        } else if (groupNo != null && !existKeyword) {
            pageList = repository.findByMediaGroup_No(groupNo, pageable);
        } else if (groupNo == null && existKeyword) {
            pageList = repository.findByQuestionContaining(searchKeyword, pageable);
        } else {
            pageList = repository.findByMediaGroup_NoOrQuestionContaining(groupNo, searchKeyword, pageable);
        }

        return pageList.map(mediaQuiz -> mapper.map(mediaQuiz, MediaQuizDTO.class));
    }

    // 그룹별 퀴즈 조회
    public List<MediaQuizDTO> getSelectRandomInGroup(int mediaGroupNo, int quizNumber) {
        List<MediaQuiz> entityList = repository.findAllByMediaGroup_No(mediaGroupNo);

        if (entityList.size() < quizNumber) {
            throw new IllegalStateException();
        }

        Collections.shuffle(entityList);

        List<MediaQuizDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < quizNumber; i++) {
            dtoList.add(mapper.map(entityList.get(i), MediaQuizDTO.class));
        }

        return dtoList;
    }

    // 번호들로 리스트 조회
    public List<MediaQuizDTO> getSelectAllInNoList(List<Integer> noList) {
        List<MediaQuizDTO> quizList = new ArrayList<>();

        for (int quizNo : noList) {
            Optional<MediaQuiz> om = repository.findById(quizNo);

            if (om.isPresent()) {
                MediaQuiz mediaQuiz = om.get();
                quizList.add(mapper.map(mediaQuiz, MediaQuizDTO.class));
            }
        }

        return quizList;
    }

    public MediaQuizDTO getSelectOne(MediaQuizDTO mediaQuizDTO) {
        Optional<MediaQuiz> om = repository.findById(mediaQuizDTO.getNo());

        if (om.isEmpty()) {
            return null;
        }

        MediaQuiz mediaQuiz = om.get();
        return mapper.map(mediaQuiz, MediaQuizDTO.class);
    }

    // 0813 수정(TJ)
    public void setInsert(MediaQuizDTO mediaQuizDTO) {

        // 참조값(그룹)이 있는지 확인 후에 DTO에 set
        Optional<MediaGroup> om = mgRepository.findById(mediaQuizDTO.getMediaGroup().getNo());
        if (om.isPresent()) {
            MediaGroup mediaGroup = om.get();
            MediaGroupDTO mediaGroupDTO = mapper.map(mediaGroup, MediaGroupDTO.class);
            mediaQuizDTO.setMediaGroup(mediaGroupDTO);
        }
        repository.save(mapper.map(mediaQuizDTO, MediaQuiz.class));
    }

    public void setUpdate(MediaQuizDTO mediaQuizDTO) {
        repository.save(mapper.map(mediaQuizDTO, MediaQuiz.class));
    }

    public void setDelete(MediaQuizDTO mediaQuizDTO) {
        repository.delete(mapper.map(mediaQuizDTO, MediaQuiz.class));
    }

    // 정답 조합 리스트
    public List<String> AnswerCombineList(List<List<String>> answerCharListBox) {
        List<String> answerCombineList = new ArrayList<>();

        for (List<String> answerCharList : answerCharListBox) {
            StringBuilder stringBuilder = new StringBuilder();

            for (String answerChar : answerCharList) {
                if (answerChar != null && !answerChar.trim().isEmpty()) {
                    stringBuilder.append(answerChar.trim());
                }
            }
            answerCombineList.add(stringBuilder.toString());
        }

        return answerCombineList;
    }

    // 채점
    public int correctCountCalculator(List<MediaQuizDTO> quizList, List<List<String>> answerCharListBox) {
        List<String> answerCombineList = AnswerCombineList(answerCharListBox);

        int correctCount = 0;
        for (int i = 0; i < quizList.size(); i++) {
            String correctAnswer = quizList.get(i).getAnswer();

            if (correctAnswer != null && answerCombineList.get(i).equals(correctAnswer.trim())) {
                correctCount++;
            }
        }

        return correctCount;
    }

}

