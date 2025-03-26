package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.course.CourseRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.entities.core.Course;
import org.springframework.http.ResponseEntity;

public interface CourseService {
    ResponseEntity<ApiResponse> createCourse(CourseRequest request);

    ResponseEntity<ApiResponse> getAllCourses();

    ResponseEntity<ApiResponse> getCourseById(int id);

    ResponseEntity<ApiResponse> updateCourse(Course course);

    ResponseEntity<ApiResponse> deleteCourse(Course course);
}
