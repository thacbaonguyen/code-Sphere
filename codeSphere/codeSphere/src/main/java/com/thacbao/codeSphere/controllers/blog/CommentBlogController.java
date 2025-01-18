package com.thacbao.codeSphere.controllers.blog;

import com.thacbao.codeSphere.dto.request.blog.CommentBlogReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.CommentBlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/comment-blog")
@RequiredArgsConstructor
public class CommentBlogController {

    private final CommentBlogService commentBlogService;
    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insertComment(@RequestBody CommentBlogReq request) {

        return commentBlogService.insertComment(request);
    }

    @GetMapping("/all/{blog-id}")
    public ResponseEntity<ApiResponse> viewAllCommentWithBlog(@PathVariable("blog-id") Integer blogId) {

        return commentBlogService.viewAllCommentWithBlog(blogId);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateComment(@PathVariable("id") Integer id, @RequestBody CommentBlogReq request) {
        return commentBlogService.updateComment(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable("id") Integer id) {
        return commentBlogService.deleteComment(id);
    }
}
