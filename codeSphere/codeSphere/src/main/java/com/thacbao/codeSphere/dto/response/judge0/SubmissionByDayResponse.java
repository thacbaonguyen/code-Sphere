package com.thacbao.codeSphere.dto.response.judge0;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionByDayResponse {

    private LocalDate createdAt;

    private Long count;
}
