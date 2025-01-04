package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.ExerciseRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface ExerciseService {
    ResponseEntity<ApiResponse> insertExercise(ExerciseRequest request);

    ResponseEntity<ApiResponse> getAllExercises();
}
