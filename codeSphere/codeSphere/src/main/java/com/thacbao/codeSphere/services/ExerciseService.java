package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.exercise.ExerciseReq;
import com.thacbao.codeSphere.dto.request.exercise.ExerciseUdReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.rmi.AlreadyBoundException;
import java.util.Map;

public interface ExerciseService {
    ResponseEntity<ApiResponse> insertExercise(ExerciseReq request) throws AlreadyBoundException;

    ResponseEntity<ApiResponse> viewExerciseDetails(String code);

    ResponseEntity<ApiResponse> filterExerciseBySubjectAndParam(String subject, String order, String by, String search, Integer page);

    ResponseEntity<ApiResponse> getTotalPage(String subject, String order, String by, String search);

    ResponseEntity<ApiResponse> activateExercise(Map<String, String> request);

    ResponseEntity<ApiResponse> updateExercise(ExerciseUdReq request);

    ResponseEntity<ApiResponse> deleteExercise(String code);


}
