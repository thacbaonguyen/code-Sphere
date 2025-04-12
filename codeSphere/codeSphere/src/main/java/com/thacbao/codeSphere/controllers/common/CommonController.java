package com.thacbao.codeSphere.controllers.common;
import com.thacbao.codeSphere.data.repository.exercise.ContributeRepository;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.common.CommonResponse;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/common")
@RequiredArgsConstructor
public class CommonController {
    private final ContributeRepository contributeRepository;

    @GetMapping("/total-comment")
    public ResponseEntity<ApiResponse> totalComment() {
        CommonResponse commonResponse = contributeRepository.commentWithUser();
        return CodeSphereResponses.generateResponse(commonResponse, "Count comment success", HttpStatus.OK);
    }

    @GetMapping("/total-contribute")
    public ResponseEntity<ApiResponse> totalContribute() {
        List<CommonResponse> responses = contributeRepository.totalContribute();
        return CodeSphereResponses.generateResponse(responses, "Count contribute success", HttpStatus.OK);
    }

    @GetMapping("/total-file-storage")
    public ResponseEntity<ApiResponse> totalFileStorage() {
        CommonResponse response = contributeRepository.fileStoreWithUser();
        return CodeSphereResponses.generateResponse(response, "Count file storage success", HttpStatus.OK);
    }
}
