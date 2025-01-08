package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.CmExReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.sql.SQLDataException;
import java.util.Map;

public interface CommentService {
    ResponseEntity<ApiResponse> insertComment(CmExReq request);

    ResponseEntity<ApiResponse> getCommentEx(Integer exerciseId) throws SQLDataException;

    ResponseEntity<ApiResponse> updateComment(Map<String, String> request) throws SQLDataException;

    ResponseEntity<ApiResponse> getCommentHistory(Integer commentExerciseId) throws SQLDataException;
}
