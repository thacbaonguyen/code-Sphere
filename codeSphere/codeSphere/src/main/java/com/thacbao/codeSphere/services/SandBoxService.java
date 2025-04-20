package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.sandbox.SubmissionRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface SandBoxService {

    ResponseEntity<ApiResponse> createSubmission(SubmissionRequest request);
}
