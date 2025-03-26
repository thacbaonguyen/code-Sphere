package com.thacbao.codeSphere.services.courseImpl;

import com.thacbao.codeSphere.dto.request.course.SectionRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.SectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class SectionServiceImpl implements SectionService {
    @Override
    public ResponseEntity<ApiResponse> createSection(SectionRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> getAllSection(Integer courseId) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> updateSection(Integer sectionId, SectionRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> deleteSection(Integer sectionId) {
        return null;
    }
}
