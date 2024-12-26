package com.thacbao.codeSphere.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CodeSphereResponse {
    public static ResponseEntity<ApiResponse> generateResponse(ApiResponse response, HttpStatus status) {

        return new ResponseEntity<>(response, status);
    }
}
