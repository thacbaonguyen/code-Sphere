package com.thacbao.codeSphere.controllers.blog;

import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.blogImpl.CommentBlogHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comment-blog-history")
@RequiredArgsConstructor
public class CmtBlogHistoryController {

    private final CommentBlogHistoryService commentBlogHistoryService;

    @GetMapping("/all/{comment-blog-id}")
    public ResponseEntity<ApiResponse> getAll(@PathVariable("comment-blog-id") Integer commentBlogId) {
        return commentBlogHistoryService.getAllCommentBlogHistory(commentBlogId);
    }
}
