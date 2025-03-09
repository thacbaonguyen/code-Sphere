package com.thacbao.codeSphere.controllers.judge0;
import com.thacbao.codeSphere.dto.request.judge0.SubmissionRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.Judge0Service;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/judge0/submission")
@RequiredArgsConstructor
public class Judge0Controller {
    private final Judge0Service judge0Service;

    @PostMapping("/grade")
    public ResponseEntity<ApiResponse> judge0(@RequestBody SubmissionRequest request) {
        return judge0Service.createSubmission(request);
    }
}
