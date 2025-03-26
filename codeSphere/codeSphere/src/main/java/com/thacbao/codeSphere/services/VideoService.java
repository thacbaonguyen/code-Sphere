package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.course.VideoRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface VideoService {
    ResponseEntity<ApiResponse> createVideo(VideoRequest request);

    ResponseEntity<ApiResponse> getAllVideo(Integer sectionId);

    ResponseEntity<ApiResponse> updateVideo(Integer videoId, VideoRequest request);

    ResponseEntity<ApiResponse> deleteVideo(Integer videoId);
}
