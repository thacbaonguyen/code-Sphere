package com.thacbao.codeSphere.services.courseImpl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.thacbao.codeSphere.configurations.JwtFilter;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final ModelMapper modelMapper;
    private final AmazonS3 amazonS3;
    private final JwtFilter jwtFilter;

    @Value("${cloud.aws.s3.bucketFeature}")
    private String bucketFeature;
    @Override
    public ResponseEntity<ApiResponse> createVideo(VideoRequest request) {
        Video video = modelMapper.map(request, Video.class);
        video.setCreatedAt(LocalDate.now());
        videoRepository.save(video);
        return CodeSphereResponses.generateResponse(null, "Create video success", HttpStatus.CREATED);
    }

    @Override
    public void uploadVideo(Integer videoId, MultipartFile file) {
        Video video = videoRepository.findById(videoId).orElseThrow(
                () -> new NotFoundException("Video not found")
        );
        try {
            String urlS3 = uploadToS3(file);
            video.setS3url(urlS3);
            videoRepository.save(video);
        }
        catch (Exception e) {
            log.error("Failed to upload video: {}", e.getMessage(), e.getCause());
        }
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

    /**
     * Upload file lên cloud
     * @param file
     * @return
     * @throws IOException
     */
    private String uploadToS3(MultipartFile file) throws IOException {
        File compressedFile = compressVideo(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(compressedFile.length());

        amazonS3.putObject(bucketFeature, fileName, new FileInputStream(compressedFile), metadata);
        return generateSignedUrl(fileName);
    }
    private File compressVideo(MultipartFile file) throws IOException {
        File inputFile = File.createTempFile("input_", file.getOriginalFilename());
        file.transferTo(inputFile);

        String outputFileName = "compressed_" + System.currentTimeMillis() + ".mp4";
        File outputFile = new File(outputFileName);

        //fgfmpeg nén video
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", inputFile.getAbsolutePath(),
                "-vcodec", "h264", "-acodec", "aac",
                "-b:v", "1000k",
                outputFile.getAbsolutePath()
        );
        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            throw new RuntimeException("Failed to compress video: " + e.getMessage());
        }
        inputFile.delete();
        return outputFile;
    }

    private String generateSignedUrl(String fileName) {
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketFeature, fileName)
                .withMethod(HttpMethod.GET)
                .withExpiration(new Date(System.currentTimeMillis() + 3600 * 1000));
        return amazonS3.generatePresignedUrl(request).toString();
    }

    /**
     * delete file trên cloud
     * @param oldFileName
     */
    private void deleteFromS3(String oldFileName) {
        amazonS3.deleteObject(bucketFeature, oldFileName);
    }
}
