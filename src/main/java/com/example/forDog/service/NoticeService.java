package com.example.forDog.service;

import com.example.forDog.dto.NoticeDTO;
import com.example.forDog.entity.Notice;
import com.example.forDog.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository repository;
    private final ModelMapper mapper;

    public List<NoticeDTO> getSelectAll() {
        List<Notice> entityList = repository.findAll();
        List<NoticeDTO> dtoList = new ArrayList<>();
        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), NoticeDTO.class));
        }
        return dtoList;
    }

    public Page<NoticeDTO> getSelectAll(int page, String searchKeyword) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("no")));
        Page<Notice> pageList;

        boolean existKeyword = searchKeyword != null && !searchKeyword.isEmpty();

        if (!existKeyword) {
            pageList = repository.findAll(pageable);
        } else {
            pageList = repository.findBySubjectContaining(searchKeyword, pageable);
        }

        return pageList.map(notice -> mapper.map(notice, NoticeDTO.class));
    }


    public Page<NoticeDTO> getSelectAll(int page, String searchType, String keyword) {
        Pageable pageable = PageRequest.of(page, 10, Sort.by(Sort.Order.desc("no")));
        Page<Notice> pageList;

        boolean noKeyword = (searchType == null || searchType.isEmpty()) &&
                (keyword == null || keyword.isEmpty());
        boolean noSearch = (searchType == null || searchType.isEmpty()) &&
                (keyword != null);

        if (noKeyword) {
            pageList = repository.findAll(pageable);
        } else if (noSearch) {
            pageList = repository.findByWriterContainingOrSubjectContainingOrContentContaining(
                    keyword, keyword, keyword, pageable);
        } else {
            switch (searchType) {
                case "subject":
                    pageList = repository.findBySubjectContaining(keyword, pageable);
                    break;
                case "writer":
                    pageList = repository.findByWriterContaining(keyword, pageable);
                    break;
                case "content":
                    pageList = repository.findByContentContaining(keyword, pageable);
                    break;
                default:
                    pageList = repository.findAll(pageable);
            }
        }

        return pageList.map(notice -> mapper.map(notice, NoticeDTO.class));


    }

    public NoticeDTO getSelectOne(NoticeDTO noticeDTO) {
        Optional<Notice> on = repository.findById(noticeDTO.getNo());

        if (on.isEmpty()) {
            return null;
        }

        Notice notice = on.get();
        return mapper.map(notice, NoticeDTO.class);
    }

    public NoticeDTO getSelectOne(int no) {
        Optional<Notice> on = repository.findById(no);

        if (on.isEmpty()) {
            return null;
        }

        Notice notice = on.get();
        return mapper.map(notice, NoticeDTO.class);
    }

    // 최신 3개의 공지사항 가져오기(TJ)
    public List<NoticeDTO> getSelectTop3(NoticeDTO noticeDTO) {
        List<Notice> entityList = repository.findTop3ByOrderByRegiDateDesc();
        List<NoticeDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), NoticeDTO.class));
        }
        return dtoList;
    }

    public List<NoticeDTO> getSelectTop5() {
        List<Notice> entityList = repository.findTop5ByOrderByRegiDateDesc();
        List<NoticeDTO> dtoList = new ArrayList<>();

        for (int i = 0; i < entityList.size(); i++) {
            dtoList.add(mapper.map(entityList.get(i), NoticeDTO.class));
        }
        return dtoList;

    }

    // 이전글
    public NoticeDTO getSelectPrev(int no, String searchKeyword) {
        boolean existKeyword = searchKeyword != null && !searchKeyword.isEmpty();

        Optional<Notice> on;

        if (!existKeyword) {
            on = repository.findTopByNoGreaterThanOrderByNoAsc(no);
        } else {
            on = repository.findTopByNoGreaterThanAndSubjectContainingOrderByNoAsc(no, searchKeyword);
        }

        if (on.isEmpty()) {
            return null;
        }

        Notice notice = on.get();
        return mapper.map(notice, NoticeDTO.class);
    }

    // 다음글
    public NoticeDTO getSelectNext(int no, String searchKeyword) {
        boolean existKeyword = searchKeyword != null && !searchKeyword.isEmpty();

        Optional<Notice> on;

        if (!existKeyword) {
            on = repository.findTopByNoLessThanOrderByNoDesc(no);
        } else {
            on = repository.findTopByNoLessThanAndSubjectContainingOrderByNoDesc(no, searchKeyword);
        }

        if (on.isEmpty()) {
            return null;
        }

        Notice notice = on.get();
        return mapper.map(notice, NoticeDTO.class);
    }


    public void setInsert(NoticeDTO noticeDTO) {
        repository.save(mapper.map(noticeDTO, Notice.class));
    }

    public void setUpdate(NoticeDTO noticeDTO) {
        repository.save(mapper.map(noticeDTO, Notice.class));
    }

    public void setDelete(NoticeDTO noticeDTO) {
        repository.delete(mapper.map(noticeDTO, Notice.class));
    }

    public void updateCount(int no) {
        Optional<Notice> on = repository.findById(no);

        if(on.isPresent()) {
            Notice notice = on.get();
            notice.setCount(notice.getCount() + 1);
            repository.save(notice);
        }
    }

}
