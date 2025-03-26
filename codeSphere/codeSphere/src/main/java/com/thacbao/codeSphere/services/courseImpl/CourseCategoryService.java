package com.thacbao.codeSphere.services.courseImpl;

import com.thacbao.codeSphere.data.repository.course.CourseCategoryRepo;
import com.thacbao.codeSphere.dto.request.course.CourseCategoryRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.entities.reference.CourseCategory;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CourseCategoryService {

    private final CourseCategoryRepo courseCategoryRepo;

    public ResponseEntity<ApiResponse> create(CourseCategoryRequest request) {
        CourseCategory courseCategory = new CourseCategory();
        courseCategory.setName(request.getName());
        if (request.getDescription() != null) {
            courseCategory.setDescription(request.getDescription());
        }
        courseCategoryRepo.save(courseCategory);
        return CodeSphereResponses.generateResponse(null, "Create category success", HttpStatus.CREATED);
    }
}
