package com.thacbao.codeSphere.controllers.course;

import com.thacbao.codeSphere.dto.request.course.CourseCategoryRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.courseImpl.CourseCategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/course-category")
@RequiredArgsConstructor
public class CourseCategoryController {

    private final CourseCategoryService courseCategoryService;
    @PostMapping
    public ResponseEntity<ApiResponse> createCourseCategory(@RequestBody CourseCategoryRequest request) {
        return courseCategoryService.create(request);
    }
}
