package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.blog.BlogReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.sql.SQLDataException;

public interface BlogService {
    ResponseEntity<ApiResponse> insertBlog(BlogReq request);

    ResponseEntity<ApiResponse> viewBlog(String slug);
}
