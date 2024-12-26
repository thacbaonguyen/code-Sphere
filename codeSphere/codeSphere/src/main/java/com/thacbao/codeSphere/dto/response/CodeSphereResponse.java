package com.thacbao.codeSphere.dto.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class CodeSphereResponse {
    public static ResponseEntity<?> generateResponse(String message, HttpStatus status) {
        Map<String, String> map = new HashMap<>();
        map.put("message", message);
        return new ResponseEntity<>(map, status);
    }
}
