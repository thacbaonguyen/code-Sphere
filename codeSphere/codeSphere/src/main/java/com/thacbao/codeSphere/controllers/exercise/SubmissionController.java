package com.thacbao.codeSphere.controllers.exercise;

import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.data.repository.exercise.SubmissionRepository;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.sandbox.SubmissionByDayResponse;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/submission")
public class SubmissionController {

    private final SubmissionRepository submissionRepository;
    private final CustomUserDetailsService userDetailsService;
    @GetMapping("/count-by-day")
    public ResponseEntity<ApiResponse> submissionByDay(){
        List<SubmissionByDayResponse> response = submissionRepository.countSubmissionsByDayForUser(userDetailsService.getUserDetails().getId());
        return CodeSphereResponses.generateResponse(response, "ok", HttpStatus.OK);
    }
}
