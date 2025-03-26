package com.thacbao.codeSphere.controllers.course;

import com.thacbao.codeSphere.dto.request.course.CourseRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.CourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insert(@RequestBody CourseRequest request) {
        return courseService.createCourse(request);
    }
    @PostMapping("/upload/thumbnail-image/{course-id}")
    public void uploadFeatureImage(@PathVariable("course-id") Integer courseId,
                                   @RequestParam("featureImage") MultipartFile file) {
        courseService.uploadThumbnail(courseId, file);
    }

    @GetMapping("/all-courses")
    public ResponseEntity<ApiResponse> findAllCourses(@RequestParam(required = false) String search,
                                                    @RequestParam(defaultValue = "1") Integer page,
                                                    @RequestParam(defaultValue = "15") Integer pageSize,
                                                    @RequestParam(required = false) String order,
                                                    @RequestParam(required = false) String by){

        return courseService.getAllCourses(search, page, pageSize, order, by);
    }

    @GetMapping("/all-course/author/{categoryId}")
    public ResponseEntity<ApiResponse> findAllCourseByCategory(@PathVariable("categoryId") Integer categoryId,
                                                             @RequestParam(required = false) String search,
                                                             @RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "15") Integer pageSize,
                                                             @RequestParam(required = false) String order,
                                                             @RequestParam(required = false) String by) {
        return courseService.getAllCoursesByCategory(categoryId, search, page, pageSize, order, by);
    }

    @PutMapping("/update/{courseId}")
    public ResponseEntity<ApiResponse> updateCourse(@PathVariable("courseId") Integer courseId, @RequestBody CourseRequest request) {
        return courseService.updateCourse(courseId, request);
    }

    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<ApiResponse> deleteCourse(@PathVariable("courseId") Integer courseId) {
        return courseService.deleteCourse(courseId);
    }
}
