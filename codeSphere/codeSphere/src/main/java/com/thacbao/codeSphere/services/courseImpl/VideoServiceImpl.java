package com.thacbao.codeSphere.services.courseImpl;

import com.thacbao.codeSphere.data.repository.course.VideoRepository;
import com.thacbao.codeSphere.dto.request.course.VideoRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.course.VideoDTO;
import com.thacbao.codeSphere.entities.reference.Video;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.services.VideoService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final ModelMapper modelMapper;
    @Override
    public ResponseEntity<ApiResponse> createVideo(VideoRequest request) {
        Video video = modelMapper.map(request, Video.class);
        video.setCreatedAt(LocalDate.now());
        videoRepository.save(video);
        return CodeSphereResponses.generateResponse(null, "Create video success", HttpStatus.CREATED);
    }

    @Override
    public List<VideoDTO> getAllVideo(Integer sectionId) {
        List<Video> videos = videoRepository.findBySectionId(sectionId);
        List<VideoDTO> videoDTOS = videos.stream().map(video -> modelMapper.map(video, VideoDTO.class)).collect(Collectors.toList());
        return videoDTOS;
    }

    @Override
    public ResponseEntity<ApiResponse> updateVideo(Integer videoId, VideoRequest request) {
        Video video = videoRepository.findById(videoId).orElseThrow(
                () -> new NotFoundException("Video not found")
        );
        video.setTitle(request.getTitle());
        video.setOrderIndex(request.getOrderIndex());
        videoRepository.save(video);
        return CodeSphereResponses.generateResponse(null, "Update video success", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteVideo(Integer videoId) {
        Video video = videoRepository.findById(videoId).orElseThrow(
                () -> new NotFoundException("Video not found")
        );
        videoRepository.delete(video);
        return CodeSphereResponses.generateResponse(null, "Delete video success", HttpStatus.OK);
    }
}
