package com.thacbao.codeSphere.dto.response.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContributeDTO {
    private Integer id;
    private String title;
    private String paper;
    private String input;
    private String output;
    private String note;
    private String author;
    private String createdAt;
}
