package com.thacbao.codeSphere.dto.response.exercise;

import com.thacbao.codeSphere.entities.reference.SubmissionHistory;
import com.thacbao.codeSphere.entities.reference.TestCaseHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionHistoryResponse {
    private int id;
    private String sourceCode;
    private String status;
    private int passCount;
    private int score;
    private int totalTestCases;
    private List<TestCaseHistoryResponse> testCaseHistoryResponses;

    public SubmissionHistoryResponse(SubmissionHistory submissionHistory) {
        this.id = submissionHistory.getId();
        this.sourceCode = submissionHistory.getSourceCode();
        this.status = submissionHistory.getStatus();
        this.passCount = submissionHistory.getPassCount();
        this.score = submissionHistory.getScore();
        this.totalTestCases = submissionHistory.getTotalTestCases();
        this.testCaseHistoryResponses = convertToTestCaseHistoryResponse(submissionHistory.getTestCaseHistory());
    }

    private List<TestCaseHistoryResponse> convertToTestCaseHistoryResponse(List<TestCaseHistory> testCaseHistory) {
        ArrayList<TestCaseHistoryResponse> testCaseHistoryResponses = new ArrayList<>();
        for (TestCaseHistory testCaseHistoryResponse : testCaseHistory) {
            testCaseHistoryResponses.add(new TestCaseHistoryResponse(testCaseHistoryResponse));
        }
        return testCaseHistoryResponses;
    }
}
