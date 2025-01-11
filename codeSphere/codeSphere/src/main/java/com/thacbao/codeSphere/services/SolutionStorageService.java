package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface SolutionStorageService {

    ResponseEntity<ApiResponse> uploadFile(MultipartFile file, String code);

    ResponseEntity<ApiResponse> viewFile(String filename);

    ResponseEntity<ApiResponse> getAllToList(String code);

    ResponseEntity<ApiResponse> deleteFile(Integer id);
}
