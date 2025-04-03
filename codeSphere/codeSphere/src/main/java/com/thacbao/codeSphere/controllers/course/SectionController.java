package com.thacbao.codeSphere.controllers.course;

import com.thacbao.codeSphere.dto.request.course.SectionRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.entities.reference.Section;
import com.thacbao.codeSphere.services.SectionService;
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
@RequestMapping("/api/v1/section")
@RequiredArgsConstructor
public class SectionController {
    private final SectionService sectionService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insert(@Valid @RequestBody SectionRequest request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return sectionService.createSection(request);
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<ApiResponse> getAll(@PathVariable("id") Integer id) {
        return sectionService.viewSectionDetails(id);
    }

    @PutMapping("/update/{sectionId}")
    public ResponseEntity<ApiResponse> updateSection(@Valid @RequestBody SectionRequest request,
                                                     @PathVariable("sectionId") Integer sectionId,
                                                     BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return sectionService.updateSection(sectionId, request);
    }

    @DeleteMapping("/delete/{sectionId}")
    public ResponseEntity<ApiResponse> delete(@PathVariable("sectionId") Integer sectionId) {
        return sectionService.deleteSection(sectionId);
    }

}
