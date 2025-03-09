package com.thacbao.codeSphere.dto.response.exercise;

import com.thacbao.codeSphere.entities.reference.TestCaseHistory;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TestCaseHistoryResponse {
    private int id;
    private boolean passed;
    private String testCaseExpected;
    private String output;
    private int statusId;
    private String statusName;
    private double time;
    private long memory;
    private String errorMessage;

    public TestCaseHistoryResponse(TestCaseHistory testCaseHistory) {
        this.id = testCaseHistory.getId();
        this.passed = testCaseHistory.isPassed();
        this.testCaseExpected = testCaseHistory.getTestCaseExpected();
        this.output = testCaseHistory.getOutput();
        this.statusId = testCaseHistory.getStatusId();
        this.statusName = testCaseHistory.getStatusName();
        this.time = testCaseHistory.getTime();
        this.memory = testCaseHistory.getMemory();
        this.errorMessage = testCaseHistory.getErrorMessage();
    }
}
