package com.thacbao.codeSphere.controllers.course;

import com.thacbao.codeSphere.dto.request.course.VideoRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.VideoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/video")
@RequiredArgsConstructor
public class VideoController {
    private final VideoService videoService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insert(@RequestBody VideoRequest request) {
        return videoService.createVideo(request);
    }

    @PostMapping(value = "/upload/{videoId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String uploadVideo(@PathVariable("videoId") Integer videoId,
                                                   @RequestParam("file") MultipartFile file) {
        videoService.uploadVideo(videoId, file);
        return "success";
    }

    @GetMapping("/video-detail/{id}")
    public ResponseEntity<ApiResponse> videoDetail(@PathVariable("id") Integer id) {
        return videoService.viewDetail(id);
    }

    @PutMapping("/update/{videoId}")
    public ResponseEntity<ApiResponse> update(@PathVariable("videoId") Integer videoId, @RequestBody VideoRequest request) {
        return videoService.updateVideo(videoId, request);
    }
    @DeleteMapping("/delete/{videoId}")
    public ResponseEntity<ApiResponse> update(@PathVariable("videoId") Integer videoId) {
        return videoService.deleteVideo(videoId);
    }

}
