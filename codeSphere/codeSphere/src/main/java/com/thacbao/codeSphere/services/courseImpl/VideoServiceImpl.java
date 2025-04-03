package com.thacbao.codeSphere.services.courseImpl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.data.repository.course.VideoRepository;
import com.thacbao.codeSphere.dto.request.course.VideoRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.course.VideoDTO;
import com.thacbao.codeSphere.entities.reference.Video;
import com.thacbao.codeSphere.exceptions.common.AppException;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.exceptions.user.PermissionException;
import com.thacbao.codeSphere.services.VideoService;
import com.thacbao.codeSphere.services.redis.RedisService;
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
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final VideoRepository videoRepository;
    private final ModelMapper modelMapper;
    private final AmazonS3 amazonS3;
    private final JwtFilter jwtFilter;
    private final RedisService redisService;

    @Value("${cloud.aws.s3.bucketFeature}")
    private String bucketFeature;
    @Override
    public ResponseEntity<ApiResponse> createVideo(VideoRequest request) {
        if (jwtFilter.isAdmin() || jwtFilter.isManager()){
            Video video = modelMapper.map(request, Video.class);
            video.setCreatedAt(LocalDate.now());
            video.setId(null);
            Video videoSave = videoRepository.save(video);
            Map<String, Object> response = new HashMap<>();
            response.put("id", videoSave.getId());
            redisService.delete("courseDetails:");
            return CodeSphereResponses.generateResponse(response, "Create video success", HttpStatus.CREATED);
        }
        throw new PermissionException(CodeSphereConstants.PERMISSION_DENIED);
    }

    @Override
    public void uploadVideo(Integer videoId, MultipartFile file) {
        Video video = videoRepository.findById(videoId).orElseThrow(
                () -> new NotFoundException("Video not found")
        );
        validateFile(file);
        try {
            String fileGen = genFileName(file);
            String urlS3 = uploadToS3(file, fileGen);
            video.setS3url(urlS3);
            video.setVideoUrl(fileGen);
            videoRepository.save(video);
            log.info("Upload video success");
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
    public ResponseEntity<ApiResponse> viewDetail(Integer id) {
        String cacheKey = String.format("videoDetails:%s", id);
        String cacheValue = redisService.get(cacheKey);
        if (cacheValue != null) {
            return CodeSphereResponses.generateResponse(cacheValue, "Video details", HttpStatus.OK);
        }
        Video video = videoRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Video not found")
        );
        String url = generateSignedUrl(video.getVideoUrl());
        redisService.set(cacheKey, url, 1, TimeUnit.HOURS);
        return CodeSphereResponses.generateResponse(url, "View video success", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> videoInfo(Integer id) {
        if (jwtFilter.isAdmin() || jwtFilter.isManager()){
            Video video = videoRepository.findById(id).orElseThrow(
                    () -> new NotFoundException("Video not found")
            );
            VideoDTO videoDTO = new VideoDTO(video);
            return CodeSphereResponses.generateResponse(videoDTO, "Video info success", HttpStatus.OK);
        }
        throw new PermissionException(CodeSphereConstants.PERMISSION_DENIED);
    }

    @Override
    public ResponseEntity<ApiResponse> updateVideo(Integer videoId, VideoRequest request) {
        if (jwtFilter.isAdmin() || jwtFilter.isManager()){
            Video video = videoRepository.findById(videoId).orElseThrow(
                    () -> new NotFoundException("Video not found")
            );
            video.setTitle(request.getTitle());
            video.setOrderIndex(request.getOrderIndex());
            videoRepository.save(video);
            redisService.delete("courseDetails:");
            return CodeSphereResponses.generateResponse(null, "Update video success", HttpStatus.OK);
        }
        throw new PermissionException(CodeSphereConstants.PERMISSION_DENIED);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteVideo(Integer videoId) {
        if (jwtFilter.isAdmin() || jwtFilter.isManager()){
            Video video = videoRepository.findById(videoId).orElseThrow(
                    () -> new NotFoundException("Video not found")
            );
            videoRepository.delete(video);
            deleteFromS3(video.getVideoUrl());
            redisService.delete("courseDetails:");
            return CodeSphereResponses.generateResponse(null, "Delete video success", HttpStatus.OK);
        }
        throw new PermissionException(CodeSphereConstants.PERMISSION_DENIED);
    }

    /**
     * Upload file lên cloud
     * @param file
     * @return
     * @throws IOException
     */
    private String uploadToS3(MultipartFile file, String fileGen) throws IOException {
        File compressedFile = compressVideo(file);

        try {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(compressedFile.length());

            amazonS3.putObject(bucketFeature, fileGen, new FileInputStream(compressedFile), metadata);
            return generateSignedUrl(fileGen);
        } finally {
            if (compressedFile != null && compressedFile.exists()) {
                compressedFile.delete();
            }
        }
    }
    private File compressVideo(MultipartFile file) throws IOException {
        File inputFile = File.createTempFile("input_", file.getOriginalFilename());
        file.transferTo(inputFile);

        String outputFileName = "compressed_" + System.currentTimeMillis() + ".mp4";
        File outputFile = new File(outputFileName);

        //fgfmpeg nén video
        ProcessBuilder pb = new ProcessBuilder(
                "ffmpeg", "-i", inputFile.getAbsolutePath(),
                "-vcodec", "libx264", // Sử dụng libx264 thay vì h264 để có khả năng nén tốt hơn
                "-preset", "medium", // Cân bằng giữa tốc độ mã hóa và chất lượng
                "-crf", "28", // Kiểm soát chất lượng hình ảnh (23-28 là hợp lý cho web, càng thấp càng tốt)
                "-b:v", "0", // Cho phép CRF điều khiển bitrate
                "-acodec", "aac",
                "-b:a", "48k", // Bitrate audio thấp hơn nhưng vẫn đủ cho giọng nói
                "-movflags", "+faststart", // Tối ưu cho phát trực tuyến
                outputFile.getAbsolutePath()
        );
        pb.inheritIO();
        try {
            Process process = pb.start();
            process.waitFor();
        } catch (Exception e) {
            log.error("error while compressing video: {}", e.getMessage(), e.getCause());
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

    private String genFileName(MultipartFile file) {
        return file.getOriginalFilename() + "_" + LocalDateTime.now();
    }

    /**
     * Validate file trước khi up lên cloud
     * @param file
     * @return
     */
    private boolean validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException("File is empty");
        }
        if (file.getSize() == 0){
            throw new AppException("File is empty");
        }
        String contentType = file.getContentType();
        if (!contentType.startsWith("video/")) {
            throw new AppException("File is not an video");
        }
        long maxSize = 150 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new AppException("File is too large");
        }
        return true;
    }

    /**
     * delete file trên cloud
     * @param oldFileName
     */
    private void deleteFromS3(String oldFileName) {
        amazonS3.deleteObject(bucketFeature, oldFileName);
    }
}
