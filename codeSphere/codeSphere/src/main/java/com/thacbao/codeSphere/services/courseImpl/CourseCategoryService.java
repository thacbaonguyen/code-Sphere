package com.thacbao.codeSphere.services.courseImpl;

import com.thacbao.codeSphere.data.repository.course.CourseCategoryRepo;
import com.thacbao.codeSphere.dto.request.course.CourseCategoryRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.entities.reference.CourseCategory;
import com.thacbao.codeSphere.exceptions.common.AlreadyException;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CourseCategoryService {

    private final CourseCategoryRepo courseCategoryRepo;

    public ResponseEntity<ApiResponse> create(CourseCategoryRequest request) {
        CourseCategory categoryExist = courseCategoryRepo.findByName(request.getName());
        if (categoryExist != null) {
            throw new AlreadyException("This category already exist");
        }
        CourseCategory courseCategory = new CourseCategory();
        courseCategory.setName(request.getName());
        if (request.getDescription() != null) {
            courseCategory.setDescription(request.getDescription());
        }
        courseCategoryRepo.save(courseCategory);
        return CodeSphereResponses.generateResponse(null, "Create category success", HttpStatus.CREATED);
    }

    public ResponseEntity<ApiResponse> getAllCategories(){
        List<CourseCategory> list = courseCategoryRepo.findAll();
        return CodeSphereResponses.generateResponse(list, "Get all categories", HttpStatus.OK);
    }
}
