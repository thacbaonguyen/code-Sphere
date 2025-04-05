package com.thacbao.codeSphere.controllers.course;

import com.thacbao.codeSphere.dto.request.course.CourseRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.CourseService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/course")
@RequiredArgsConstructor
public class CourseController {
    private final CourseService courseService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insert(@Valid @RequestBody CourseRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return courseService.createCourse(request);
    }
    @PostMapping("/upload/thumbnail-image/{course-id}")
    public void uploadFeatureImage(@PathVariable("course-id") Integer courseId,
                                   @RequestParam("thumbnail") MultipartFile file) {
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

    @GetMapping("/all-course/category/{categoryId}")
    public ResponseEntity<ApiResponse> findAllCourseByCategory(@PathVariable(value = "categoryId", required = false) Integer categoryId,
                                                             @RequestParam(required = false) String search,
                                                             @RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "15") Integer pageSize,
                                                             @RequestParam(required = false) String order,
                                                             @RequestParam(required = false) String by) {
        return courseService.getAllCoursesByCategory(categoryId, search, page, pageSize, order, by);
    }

    @GetMapping("/course-details/{id}")
    public ResponseEntity<ApiResponse> findCourseById(@PathVariable("id") Integer id) {
        return courseService.getCourseById(id);
    }

    @PutMapping("/update/{courseId}")
    public ResponseEntity<ApiResponse> updateCourse(@Valid @PathVariable("courseId") Integer courseId,
                                                    @RequestBody CourseRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return courseService.updateCourse(courseId, request);
    }

    @DeleteMapping("/delete/{courseId}")
    public ResponseEntity<ApiResponse> deleteCourse(@PathVariable("courseId") Integer courseId) {
        return courseService.deleteCourse(courseId);
    }
}
