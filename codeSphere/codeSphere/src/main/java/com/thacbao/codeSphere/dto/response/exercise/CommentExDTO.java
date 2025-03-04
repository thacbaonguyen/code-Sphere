package com.thacbao.codeSphere.dto.response.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentExDTO {
    private Integer id;

    private String content;

    private String author;

    private String fullName;

    private String createdAt;

    private String updatedAt;
}
