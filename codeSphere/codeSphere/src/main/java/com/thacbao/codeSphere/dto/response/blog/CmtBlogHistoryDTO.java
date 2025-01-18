package com.thacbao.codeSphere.dto.response.blog;

import com.thacbao.codeSphere.entities.reference.CmtBlogHistory;
import lombok.Data;

@Data
public class CmtBlogHistoryDTO {
    private Integer id;

    private Integer commentId;

    private String content;

    private String updatedAt;

    public CmtBlogHistoryDTO(CmtBlogHistory cmtBlogHistory) {
        this.id = cmtBlogHistory.getId();
        this.commentId = cmtBlogHistory.getCommentBlog().getId();
        this.content = cmtBlogHistory.getContent();
        this.updatedAt = cmtBlogHistory.getUpdatedAt().toString();
    }
}
