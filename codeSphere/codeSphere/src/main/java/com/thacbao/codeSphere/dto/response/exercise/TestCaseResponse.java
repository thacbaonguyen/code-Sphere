package com.thacbao.codeSphere.dto.response.exercise;

import com.thacbao.codeSphere.entities.reference.TestCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TestCaseResponse {
    private int id;
    private String input;
    private String output;
    private Integer exerciseId;

    public TestCaseResponse(TestCase testCase) {
        this.id = testCase.getId();
        this.input = testCase.getInput();
        this.output = testCase.getOutput();
        this.exerciseId = testCase.getExercise().getId();
    }
}
