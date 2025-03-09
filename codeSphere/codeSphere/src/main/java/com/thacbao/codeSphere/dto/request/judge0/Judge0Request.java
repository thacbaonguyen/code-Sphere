package com.thacbao.codeSphere.dto.request.judge0;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Judge0Request {
    private String source_code;
    private Integer language_id;
    private String stdin;
    private String expected_output;
}
