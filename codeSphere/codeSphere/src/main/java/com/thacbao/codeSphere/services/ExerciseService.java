package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.ExerciseRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ExerciseService {
    ResponseEntity<ApiResponse> insertExercise(ExerciseRequest request);

    ResponseEntity<ApiResponse> getAllExercises();

    ResponseEntity<ApiResponse> filterExerciseBySubject(Map<String, String> request);

    ResponseEntity<ApiResponse> viewExerciseDetails(String code);
}
