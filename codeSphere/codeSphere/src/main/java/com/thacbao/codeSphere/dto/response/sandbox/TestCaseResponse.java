package com.thacbao.codeSphere.dto.response.sandbox;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseResponse {
    private Integer testCaseId;

    private boolean passed;

    private String testCaseExpected;

    private String result;

    private SubmissionResponse submissionResponse;
}
