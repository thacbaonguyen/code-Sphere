package com.thacbao.codeSphere.dto.response.judge0;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FinalResponse {
    private String status;
    private int passedCount;
    private int score;
    private int totalTestCases;
    private ArrayList<TestCaseResponse> listTestCaseResponses;
}
