package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.CmExRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface CommentService {
    ResponseEntity<ApiResponse> insertComment(CmExRequest request);

    ResponseEntity<ApiResponse> getCommentEx(Integer exerciseId);

    ResponseEntity<ApiResponse> updateComment(Map<String, String> request);

    ResponseEntity<ApiResponse> getCommentHistory(Integer commentExerciseId);
}
