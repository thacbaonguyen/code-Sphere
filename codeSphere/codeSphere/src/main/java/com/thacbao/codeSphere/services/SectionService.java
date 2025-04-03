package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.course.SectionRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.course.SectionDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SectionService {
    ResponseEntity<ApiResponse> createSection(SectionRequest request);

    List<SectionDTO> getAllSection(Integer courseId);

    ResponseEntity<ApiResponse> viewSectionDetails(Integer id);

    ResponseEntity<ApiResponse> updateSection(Integer sectionId, SectionRequest request);

    ResponseEntity<ApiResponse> deleteSection(Integer sectionId);
}
