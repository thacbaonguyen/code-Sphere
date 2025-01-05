package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.ExerciseRequest;
import com.thacbao.codeSphere.dto.request.ExerciseUpdateRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ExerciseService {
    ResponseEntity<ApiResponse> insertExercise(ExerciseRequest request);

    ResponseEntity<ApiResponse> filterExerciseBySubject(Map<String, String> request);

    ResponseEntity<ApiResponse> viewExerciseDetails(String code);

    ResponseEntity<ApiResponse> filterExBySubjectAndOrder( Map<String, String> request, String order, String by);

    ResponseEntity<ApiResponse> activateExercise(Map<String, String> request);

    ResponseEntity<ApiResponse> updateExercise(ExerciseUpdateRequest request);

    ResponseEntity<ApiResponse> deleteExercise(String code);


}
