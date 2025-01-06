package com.thacbao.codeSphere.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentExDTO {
    private String content;

    private String author;

    private String createdAt;

    private String updatedAt;
}
