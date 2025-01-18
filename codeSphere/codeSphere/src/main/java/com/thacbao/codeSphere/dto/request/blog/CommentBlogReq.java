package com.thacbao.codeSphere.dto.request.blog;

import lombok.Data;

@Data
public class CommentBlogReq {
    private Integer blogId;

    private Integer parentId;

    private String content;
}
