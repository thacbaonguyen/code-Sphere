package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.course.SectionRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface SectionService {
    ResponseEntity<ApiResponse> createSection(SectionRequest request);

    ResponseEntity<ApiResponse> getAllSection(Integer courseId);

    ResponseEntity<ApiResponse> updateSection(Integer sectionId, SectionRequest request);

    ResponseEntity<ApiResponse> deleteSection(Integer sectionId);
}
