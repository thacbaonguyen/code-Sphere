package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.judge0.Judge0Request;
import com.thacbao.codeSphere.dto.request.judge0.SubmissionRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface Judge0Service {

    ResponseEntity<ApiResponse> createSubmission(SubmissionRequest request);
}
