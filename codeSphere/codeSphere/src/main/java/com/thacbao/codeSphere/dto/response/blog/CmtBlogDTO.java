package com.thacbao.codeSphere.dto.response.blog;

import com.thacbao.codeSphere.dto.response.user.UserDTO;
import com.thacbao.codeSphere.entities.reference.CommentBlog;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CmtBlogDTO {
    private Integer id;
    private Integer blogId;
    private Integer parentId;
    private UserDTO author;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public CmtBlogDTO(CommentBlog commentBlog) {
        this.id = commentBlog.getId();
        this.blogId = commentBlog.getBlog().getId();
        this.parentId = commentBlog.getParentComment() != null ? commentBlog.getParentComment().getId() : null;
        this.author = new UserDTO(commentBlog.getUser());
        this.content = commentBlog.getContent();
        this.createdAt = commentBlog.getCreatedAt();
        this.updatedAt = commentBlog.getUpdatedAt();
    }
}
