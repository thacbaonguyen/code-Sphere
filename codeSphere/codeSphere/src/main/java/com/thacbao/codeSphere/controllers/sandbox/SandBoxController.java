package com.thacbao.codeSphere.controllers.sandbox;
import com.thacbao.codeSphere.dto.request.sandbox.SubmissionRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.SandBoxService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/judge0/submission")
@RequiredArgsConstructor
public class SandBoxController {
    private final SandBoxService sandBoxService;

    @PostMapping("/grade")
    public ResponseEntity<ApiResponse> judge0(@RequestBody SubmissionRequest request) {
        return sandBoxService.createSubmission(request);
    }
}
