package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.blog.CommentBlogReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface CommentBlogService {
    ResponseEntity<ApiResponse> insertComment(CommentBlogReq request);

    ResponseEntity<ApiResponse> viewAllCommentWithBlog(Integer blogId);

    ResponseEntity<ApiResponse> updateComment(Integer id, CommentBlogReq request);

    ResponseEntity<ApiResponse> deleteComment(Integer id);
}
