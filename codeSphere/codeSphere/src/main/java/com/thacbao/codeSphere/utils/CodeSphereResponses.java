package com.thacbao.codeSphere.utils;

import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CodeSphereResponses {
    public static <T> ResponseEntity<ApiResponse> generateResponse(T data, String message, HttpStatus status) {
        ApiResponse apiResponse = new ApiResponse(
                status == HttpStatus.OK ? "success" : "error",
                message,
                data
        );
        return new ResponseEntity<>(apiResponse, status);
    }
}
