package com.thacbao.codeSphere.services.courseImpl;

import com.thacbao.codeSphere.dto.request.course.VideoRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {
    @Override
    public ResponseEntity<ApiResponse> createVideo(VideoRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> getAllVideo(Integer sectionId) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> updateVideo(Integer videoId, VideoRequest request) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> deleteVideo(Integer videoId) {
        return null;
    }
}
