package com.thacbao.codeSphere.controllers.course;

import com.thacbao.codeSphere.dto.request.course.CourseReview;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.CourseReviewService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/course-review")
@RequiredArgsConstructor
public class CourseReviewController {
    private final CourseReviewService courseReviewService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insertReview(@Valid @RequestBody CourseReview courseReview, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return courseReviewService.createCourseReview(courseReview);
    }

//    @GetMapping("/all-review/{courseId}")
//    public ResponseEntity<ApiResponse> getAllReview(@PathVariable("courseId") Integer courseId) {
//        return courseReviewService.getCourseReviews(courseId);
//    }
}
