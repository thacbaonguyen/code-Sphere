package com.thacbao.codeSphere.dto.request;

import lombok.Data;

@Data
public class BlogRequest {
    private String authorId;

    private String title;

    private String content;

    private String[] tags;
}
