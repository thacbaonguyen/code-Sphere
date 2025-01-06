package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.ExerciseRequest;
import com.thacbao.codeSphere.dto.request.ExerciseUdRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface ExerciseService {
    ResponseEntity<ApiResponse> insertExercise(ExerciseRequest request);

    ResponseEntity<ApiResponse> viewExerciseDetails(String code);

    ResponseEntity<ApiResponse> filterExerciseBySubjectAndParam(Map<String, String> request, String order, String by, String search, Integer page);

    ResponseEntity<ApiResponse> activateExercise(Map<String, String> request);

    ResponseEntity<ApiResponse> updateExercise(ExerciseUdRequest request);

    ResponseEntity<ApiResponse> deleteExercise(String code);


}
