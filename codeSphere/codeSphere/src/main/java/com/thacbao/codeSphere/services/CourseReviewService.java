package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.course.CourseReview;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.course.CourseReviewDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CourseReviewService {
    ResponseEntity<ApiResponse> createCourseReview(CourseReview request);

    List<CourseReviewDTO> getCourseReviews(Integer courseId);
}
