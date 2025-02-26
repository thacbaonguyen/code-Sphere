package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.blog.BlogReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;


public interface BlogService {
    ResponseEntity<ApiResponse> insertBlog(BlogReq request);

    void uploadFeatureImage(Integer blogId, MultipartFile file);

    ResponseEntity<ApiResponse> viewBlog(String slug);

    ResponseEntity<ApiResponse> getAllBlogs(String search,String isFeatured, Integer page, Integer pageSize, String order, String by, String status);

    ResponseEntity<ApiResponse> findAllByTags(String tagName, String isFeatured, Integer page, Integer pageSize, String order, String by, String status);

    ResponseEntity<ApiResponse> findMyBlogs(String search, String status, String order, String by);

    ResponseEntity<ApiResponse> updateBlog(Integer id, BlogReq request);

    ResponseEntity<ApiResponse> deleteBlog(Integer id);
}
