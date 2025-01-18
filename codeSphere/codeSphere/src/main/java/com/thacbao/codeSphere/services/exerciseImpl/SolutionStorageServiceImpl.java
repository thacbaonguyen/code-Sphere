package com.thacbao.codeSphere.services.exerciseImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.exercise.StorageDTO;
import com.thacbao.codeSphere.entities.core.Exercise;
import com.thacbao.codeSphere.entities.reference.SolutionStorage;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.exceptions.common.AppException;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.data.repository.exercise.ExerciseRepository;
import com.thacbao.codeSphere.data.repository.exercise.SolutionRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.services.SolutionStorageService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import static com.thacbao.codeSphere.constants.CodeSphereConstants.User.USER_NOT_FOUND;

@Service
@RequiredArgsConstructor
@Slf4j
public class SolutionStorageServiceImpl implements SolutionStorageService {

    private final SolutionRepository solutionRepository;

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    private final AmazonS3 amazonS3;

    private final JwtFilter jwtFilter;

    private static final List<String> FILE_EXTENSIONS = Arrays.asList("java", "py", "cpp", "txt");

    @Value("${cloud.aws.s3.bucketSolution}")
    private String bucket;

    /**
     * Lưu trữ lời giải bài tập cho bài tập cụ thể, tối đa 5 bài
     * @param file
     * @param code
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> uploadFile(MultipartFile file, String code) {
        Exercise exercise = exerciseRepository.findByCode(code);
        User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND)
        );
        if (solutionRepository.countSolution(exercise.getId(), user.getId()) > 5){
            throw new AppException("You have stored more than the allowed number of files");
        }
        try {
            if (file == null ){
                return CodeSphereResponses.generateResponse(null, "File not found", HttpStatus.BAD_REQUEST);
            }
            if (file.getSize() == 0){
                return CodeSphereResponses.generateResponse(null, "File is empty", HttpStatus.BAD_REQUEST);
            }
            String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1);
            if (!FILE_EXTENSIONS.contains(extension)){
                return CodeSphereResponses.generateResponse(null, "File extension not supported", HttpStatus.UNSUPPORTED_MEDIA_TYPE);
            }
            long maxSize = 5 * 1024 * 1024;
            if (file.getSize() > maxSize){
                return CodeSphereResponses.generateResponse(null, String.format("File is too large %d", maxSize), HttpStatus.PAYLOAD_TOO_LARGE);
            }
            String fileName = uploadToS3(file);
            SolutionStorage solutionStorage = SolutionStorage.builder()
                    .exercise(exercise)
                    .user(user)
                    .filename(fileName)
                    .fileType(file.getContentType())
                    .fileSize((int) file.getSize())
                    .build();
            solutionRepository.save(solutionStorage);
            return CodeSphereResponses.generateResponse(fileName, String.format("Successfully uploaded %s", fileName), HttpStatus.OK);
        }
        catch (Exception ex) {
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Xem lời giải trực tiếp trên client
     * @param filename
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> viewFile(String filename) {
        SolutionStorage storage = solutionRepository.findByFilename(filename).orElseThrow(
        );
        if (!storage.getUser().getUsername().equals(jwtFilter.getCurrentUsername())){
        }
        try {
            S3Object s3Object = amazonS3.getObject(bucket, filename);

            String content = new String(s3Object.getObjectContent().readAllBytes(), StandardCharsets.UTF_8);
            return CodeSphereResponses.generateResponse(content, String.format("Successfully viewed %s", filename), HttpStatus.OK);
        }
        catch (Exception ex) {
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Tải bài giải về máy
     * @param filename
     * @return
     */
    @Override
    public ResponseEntity<?> downloadFile(String filename) {
        try {
            S3Object s3Object = amazonS3.getObject(bucket, filename);
            byte[] content = IOUtils.toByteArray(s3Object.getObjectContent());

            String fileDownload = filename.substring(filename.lastIndexOf("/") + 1);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileDownload + "\"")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(content.length)
                    .body(content);

        } catch (Exception ex) {
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(LocalDateTime.now(), 500 ,null,
                            "Error downloading file: " + ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    /**
     * Hiển thị danh sách các bài tập luu trữ trên client
     * @param code
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> getAllToList(String code) {
        User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                () -> new NotFoundException(USER_NOT_FOUND)
        );
        Exercise exercise = exerciseRepository.findByCode(code);
        List<SolutionStorage> storages = solutionRepository.findByExerciseIdAndUserId(exercise.getId(), user.getId());
        List<StorageDTO> result = storages.stream().map(StorageDTO::new).toList();
        return CodeSphereResponses.generateResponse(result, String.format("Successfully viewed %s", exercise.getCode()), HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteFile(Integer id) {
        SolutionStorage storage = solutionRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Solution not found")
        );
        amazonS3.deleteObject(bucket, storage.getFilename());
        solutionRepository.delete(storage);
        return CodeSphereResponses.generateResponse(null, String.format("Successfully deleted %s", storage.getFilename()), HttpStatus.OK);
    }



    private String uploadToS3(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + LocalDate.now().toString() + file.getOriginalFilename();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        amazonS3.putObject(bucket, fileName, file.getInputStream(), objectMetadata);
        return fileName;
    }
}
