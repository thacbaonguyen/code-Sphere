package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.course.CourseRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.entities.core.Course;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface CourseService {
    ResponseEntity<ApiResponse> createCourse(CourseRequest request);

    void uploadThumbnail(Integer courseId, MultipartFile file);

    ResponseEntity<ApiResponse> getAllCourses(String search, Integer page, Integer pageSize, String order, String by);

    ResponseEntity<ApiResponse> getCourseById(int id);

    ResponseEntity<ApiResponse> getAllCoursesByCategory(Integer categoryId, String search, Integer page, Integer pageSize, String order, String by);

    ResponseEntity<ApiResponse> updateCourse(Integer courseId, CourseRequest request);

    ResponseEntity<ApiResponse> deleteCourse(Integer courseId);
}
