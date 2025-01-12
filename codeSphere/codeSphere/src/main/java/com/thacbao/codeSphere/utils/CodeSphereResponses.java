package com.thacbao.codeSphere.utils;

import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

public class CodeSphereResponses {
    public static <T> ResponseEntity<ApiResponse> generateResponse(T data, String message, HttpStatus status) {
        ApiResponse apiResponse = new ApiResponse(
                LocalDateTime.now(),
                status.value(),
                status == HttpStatus.OK ? "success" : "error",
                message,
                data
        );
        return new ResponseEntity<>(apiResponse, status);
    }
}
