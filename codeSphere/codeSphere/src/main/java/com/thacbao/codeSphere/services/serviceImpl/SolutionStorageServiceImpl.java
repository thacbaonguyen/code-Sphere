package com.thacbao.codeSphere.services.serviceImpl;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.StorageDTO;
import com.thacbao.codeSphere.entity.Exercise;
import com.thacbao.codeSphere.entity.SolutionStorage;
import com.thacbao.codeSphere.entity.User;
import com.thacbao.codeSphere.exceptions.AppException;
import com.thacbao.codeSphere.exceptions.NotFoundException;
import com.thacbao.codeSphere.exceptions.PermissionException;
import com.thacbao.codeSphere.repositories.ExerciseRepository;
import com.thacbao.codeSphere.repositories.SolutionRepository;
import com.thacbao.codeSphere.repositories.UserRepository;
import com.thacbao.codeSphere.services.SolutionStorageService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
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
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SolutionStorageServiceImpl implements SolutionStorageService {

    private final SolutionRepository solutionRepository;

    private final ExerciseRepository exerciseRepository;
    private final UserRepository userRepository;

    private final AmazonS3 amazonS3;

    private final JwtFilter jwtFilter;

    private static final List<String> FILE_EXTENSIONS = Arrays.asList("java", "py", "cpp", "txt");

    @Value("${cloud.aws.s3.bucketSolution}")
    private String bucket;

    @Override
    public ResponseEntity<ApiResponse> uploadFile(MultipartFile file, String code) {
        Exercise exercise = exerciseRepository.findByCode(code);
        User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                () -> new NotFoundException("User not found")
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
                return CodeSphereResponses.generateResponse(null, "File extension not supported", HttpStatus.BAD_REQUEST);
            }
            long maxSize = 5 * 1024 * 1024;
            if (file.getSize() > maxSize){
                return CodeSphereResponses.generateResponse(null, String.format("File is too large %d", maxSize), HttpStatus.BAD_REQUEST);
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
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

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

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse(null, "Error downloading file: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getAllToList(String code) {
        User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                () -> new NotFoundException("User not found")
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
