package com.thacbao.codeSphere.controllers;

import com.thacbao.codeSphere.dto.request.ExerciseRequest;
import com.thacbao.codeSphere.dto.request.ExerciseUpdateRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.CodeSphereResponse;
import com.thacbao.codeSphere.services.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/exercise")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insertExercise(@Valid @RequestBody ExerciseRequest request, BindingResult bindingResult) {

        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(fieldError -> {
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
                return CodeSphereResponse.generateResponse(new ApiResponse
                        ("error", "Validation failed", errors), HttpStatus.BAD_REQUEST);
            }
            return exerciseService.insertExercise(request);
        }
        catch (Exception ex) {
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/subject/question")
    public ResponseEntity<ApiResponse> filterExerciseBySubject(@RequestBody Map<String, String> request) {
        try{
            return exerciseService.filterExerciseBySubject(request);
        }
        catch (Exception ex) {
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/question/{code}")
    public ResponseEntity<ApiResponse> viewExerciseDetails(@PathVariable String code) {
        try{
            return exerciseService.viewExerciseDetails(code);
        }
        catch (Exception ex) {
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/subject/question")
    public ResponseEntity<ApiResponse> filterExBySubjectAndOrder(@RequestParam String order, @RequestParam String by,
                                                                 @RequestBody Map<String, String> request) {
        try {
            return exerciseService.filterExBySubjectAndOrder(request, order, by);
        }
        catch (Exception ex) {
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/active")
    public ResponseEntity<ApiResponse> activateExercise(@RequestBody Map<String, String> request) {
        try {
            return exerciseService.activateExercise(request);
        }
        catch (Exception ex) {
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateExercise(@Valid @RequestBody ExerciseUpdateRequest request, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                Map<String, String> errors = new HashMap<>();
                bindingResult.getFieldErrors().forEach(fieldError -> {
                    errors.put(fieldError.getField(), fieldError.getDefaultMessage());
                });
                return CodeSphereResponse.generateResponse(new ApiResponse
                        ("error", "Validation failed", errors), HttpStatus.BAD_REQUEST);
            }
            return exerciseService.updateExercise(request);
        }
        catch (Exception ex) {
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/delete/{code}")
    public ResponseEntity<ApiResponse> deleteExercise(@PathVariable String code) {
        try {
            return exerciseService.deleteExercise(code);
        }
        catch (Exception ex) {
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
