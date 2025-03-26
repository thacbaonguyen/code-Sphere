package com.thacbao.codeSphere.services.courseImpl;

import com.thacbao.codeSphere.dto.request.course.CourseRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.entities.core.Course;
import com.thacbao.codeSphere.services.CourseService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class CourseServiceImpl implements CourseService {
    @Override
    public ResponseEntity<ApiResponse> createCourse(CourseRequest request) {

        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> getAllCourses() {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> getCourseById(int id) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> updateCourse(Course course) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> deleteCourse(Course course) {
        return null;
    }
}
