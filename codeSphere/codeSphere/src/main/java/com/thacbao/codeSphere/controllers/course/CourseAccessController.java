package com.thacbao.codeSphere.controllers.course;

import com.thacbao.codeSphere.dto.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/my-course")
@RequiredArgsConstructor
public class CourseAccessController {
    private final CourseAccessService courseAccessService;
    @GetMapping("/all")
    public ResponseEntity<ApiResponse> myCourse() {
        return courseAccessService.myCourse();
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<ApiResponse> getCourseById(@PathVariable("courseId") Integer courseId) {
        return courseAccessService.getCourseById(courseId);
    }
}
