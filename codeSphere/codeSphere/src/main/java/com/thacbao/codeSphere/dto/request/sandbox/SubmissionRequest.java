package com.thacbao.codeSphere.dto.request.sandbox;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionRequest {
    private Integer exerciseId;
    private String sourceCode;
    private Integer languageId;
}
