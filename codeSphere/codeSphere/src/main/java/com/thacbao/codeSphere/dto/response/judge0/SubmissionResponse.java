package com.thacbao.codeSphere.dto.response.judge0;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmissionResponse {
    private Integer statusId;

    private String statusName;

    private double time;

    private long memory;

    private String token;

    private String errorMessage;

    private String stdout;


}
