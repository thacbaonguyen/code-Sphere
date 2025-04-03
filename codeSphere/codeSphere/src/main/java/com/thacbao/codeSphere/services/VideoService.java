package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.course.VideoRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.course.VideoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface VideoService {
    ResponseEntity<ApiResponse> createVideo(VideoRequest request);
    void uploadVideo(Integer videoId, MultipartFile file);

    List<VideoDTO> getAllVideo(Integer sectionId);
    ResponseEntity<ApiResponse> viewDetail(Integer id);
    ResponseEntity<ApiResponse> videoInfo(Integer id);
    ResponseEntity<ApiResponse> updateVideo(Integer videoId, VideoRequest request);

    ResponseEntity<ApiResponse> deleteVideo(Integer videoId);
}
