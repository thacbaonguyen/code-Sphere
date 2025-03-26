package com.thacbao.codeSphere.services.courseImpl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.data.repository.course.*;
import com.thacbao.codeSphere.data.specification.BlogSpecification;
import com.thacbao.codeSphere.data.specification.CourseSpecification;
import com.thacbao.codeSphere.dto.request.course.CourseRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.blog.BlogBriefDTO;
import com.thacbao.codeSphere.dto.response.course.CourseBriefDTO;
import com.thacbao.codeSphere.entities.core.Blog;
import com.thacbao.codeSphere.entities.core.Course;
import com.thacbao.codeSphere.entities.reference.CourseCategory;
import com.thacbao.codeSphere.exceptions.common.AppException;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.services.CourseService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseServiceImpl implements CourseService {

    private final ModelMapper modelMapper;

    private final CourseRepository courseRepository;
    private final SectionRepository sectionRepository;
    private final VideoRepository videoRepository;
    private final CourseReviewRepository courseReviewRepository;
    private final AmazonS3 amazonS3;
    
    private final JwtFilter jwtFilter;

    private final RedisTemplate<String, Object> redisTemplate;
    private final CourseCategoryRepo courseCategoryRepo;

    @Value("${cloud.aws.s3.bucketFeature}")
    private String bucketFeature;

    @Override
    public ResponseEntity<ApiResponse> createCourse(CourseRequest request) {
        Course course = modelMapper.map(request, Course.class);
        course.setCreatedAt(LocalDate.now());
        course.setRate(0);
        courseRepository.save(course);
        Map<String, Object> response = new HashMap<>();
        response.put("id", course.getId());
        redisTemplate.delete(redisTemplate.keys("allCourse:*"));
        return CodeSphereResponses.generateResponse(response, "Insert Course success", HttpStatus.CREATED);
    }

    @Override
    public void uploadThumbnail(Integer courseId, MultipartFile file) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new NotFoundException(String.format("Course with id '%d' not found", courseId))
        );
        try {
            if (validateFile(file)) {
                String oldFilename = course.getThumbnail();
                String fileName = uploadToS3(file);
                course.setThumbnail(fileName);
                courseRepository.save(course);
                if (oldFilename != null) {
                    deleteFromS3(oldFilename);
                    redisTemplate.delete(redisTemplate.keys("blog:" + course.getThumbnail()));
                    log.info("update action and clear cache blog:{}", course.getThumbnail());
                }
            }
        }
        catch (AppException | IOException e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getAllCourses(String search, Integer page, Integer pageSize, String order, String by) {
        String cacheKey = "allCourse:" + jwtFilter.getCurrentUsername() + search + (by != null ? by : "")
                + (order != null ? order : "") + page;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String cachedData = (String) ops.get(cacheKey);
            if (cachedData != null) {
                log.info("cache all {}", cacheKey);
                // Chuyển đổi dữ liệu từ cache về đối tượng mới
                Map<String, Object> responseMap = objectMapper.readValue(cachedData, Map.class);
                return CodeSphereResponses.generateResponse(responseMap, "All contribute successfully", HttpStatus.OK);
            }
            Pageable pageable = createPageable(page, pageSize, order, by);
            log.info("current page {}", page);
            log.info("current pageS {}", pageSize);
            // Create specification
            Specification<Course> spec = Specification.where(CourseSpecification.hasSearchText(search));

            Page<CourseBriefDTO> result = getCourseBriefPage(spec, pageable);
            String jsonData = objectMapper.writeValueAsString(result);
            ops.set(cacheKey, jsonData, 24, TimeUnit.HOURS);

            return CodeSphereResponses.generateResponse(result, "View all course successfully", HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
            return CodeSphereResponses.generateResponse(null, "View all course failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @Override
    public ResponseEntity<ApiResponse> getCourseById(int id) {
        return null;
    }

    @Override
    public ResponseEntity<ApiResponse> getAllCoursesByCategory(Integer categoryId, String search, Integer page, Integer pageSize, String order, String by) {
        String cacheKey = "allCourse:" + jwtFilter.getCurrentUsername() + categoryId + search + (by != null ? by : "")
                + (order != null ? order : "") + page;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String cachedData = (String) ops.get(cacheKey);
            if (cachedData != null) {
                log.info("cache all {}", cacheKey);
                // Chuyển đổi dữ liệu từ cache về đối tượng mới
                Map<String, Object> responseMap = objectMapper.readValue(cachedData, Map.class);
                return CodeSphereResponses.generateResponse(responseMap, "All contribute successfully", HttpStatus.OK);
            }
            Pageable pageable = createPageable(page, pageSize, order, by);
            log.info("current page {}", page);
            log.info("current pageS {}", pageSize);
            // Create specificationString
            Specification<Course> spec = Specification.where(CourseSpecification.hasSearchText(search))
                    .and(CourseSpecification.hasCategory(categoryId));

            Page<CourseBriefDTO> result = getCourseBriefPage(spec, pageable);
            String jsonData = objectMapper.writeValueAsString(result);
            ops.set(cacheKey, jsonData, 24, TimeUnit.HOURS);

            return CodeSphereResponses.generateResponse(result, "View all course successfully", HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
            return CodeSphereResponses.generateResponse(null, "View all course failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> updateCourse(Integer courseId, CourseRequest request) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new NotFoundException(String.format("Course with id '%d' not found", courseId))
        );
        CourseCategory category = courseCategoryRepo.findById(request.getCourseCategoryId()).orElseThrow(
                () -> new NotFoundException(String.format("Category with id '%d' not found", request.getCourseCategoryId()))
        );
        course.setTitle(request.getTitle());
        course.setDescription(request.getDescription());
        course.setPrice(request.getPrice());
        course.setDiscount(request.getDiscount());
        course.setDuration(request.getDuration());
        course.setActive(request.isActive());
        course.setCategory(category);
        courseRepository.save(course);
        return CodeSphereResponses.generateResponse(null, "Update course successfully", HttpStatus.OK);
    }

    @Override
    public ResponseEntity<ApiResponse> deleteCourse(Integer courseId) {
        Course course = courseRepository.findById(courseId).orElseThrow(
                () -> new NotFoundException(String.format("Course with id '%d' not found", courseId))
        );
        courseRepository.delete(course);
        return CodeSphereResponses.generateResponse(null, "Delete course successfully", HttpStatus.OK);
    }

    /**
     * Upload file lên cloud
     * @param file
     * @return
     * @throws IOException
     */
    private String uploadToS3(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + LocalDate.now().toString() + file.getOriginalFilename();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        amazonS3.putObject(bucketFeature, fileName, file.getInputStream(), objectMetadata);
        return fileName;
    }

    /**
     * delete file trên cloud
     * @param oldFileName
     */
    private void deleteFromS3(String oldFileName) {
        amazonS3.deleteObject(bucketFeature, oldFileName);
    }

    /**
     * Lấy thông tin hình ảnh dạng presignedUrl
     * @param fileName
     * @return
     */
    private URL viewImageFromS3(String fileName){
        try {
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            GeneratePresignedUrlRequest preSignedUrlRequest = new GeneratePresignedUrlRequest
                    (bucketFeature, fileName, HttpMethod.GET)
                    .withExpiration(expiration);
            return amazonS3.generatePresignedUrl(preSignedUrlRequest);
        }
        catch (Exception ex){
            throw new NotFoundException("Cannot find image with name " + fileName);
        }
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
        if (!contentType.startsWith("image/")) {
            throw new AppException("File is not an image");
        }
        long maxSize = 150 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new AppException("File is too large");
        }
        return true;
    }
    
    private int countSection(Integer courseId){
        return sectionRepository.countByCourseId(courseId);
    }
    
    private int countVideo(Integer sectionId){
        return videoRepository.countBySectionId(sectionId);
    }
    
    private double avgRating(Integer courseId){
        return courseReviewRepository.averageRating(courseId);
    }

    private Pageable createPageable(Integer page, Integer pageSize, String order, String by){
        Sort.Direction direction = order != null && order.equalsIgnoreCase("asc") ?
                Sort.Direction.ASC : Sort.Direction.DESC;
        String sortBy = by != null && !by.isEmpty() ? by : "createdAt";
        Pageable pageable = PageRequest.of(
                page - 1,
                pageSize,
                Sort.by(direction, sortBy)
        );
        return pageable;
    }

    private Page<CourseBriefDTO> getCourseBriefPage(Specification<Course> spec, Pageable pageable) {
        Page<CourseBriefDTO> result = courseRepository.findAll(spec, pageable)
                .map(course -> {
                    int sectionCount = countSection(course.getId());
                    int videoCount = countVideo(course.getId());
                    double avgRate = avgRating(course.getId());
                    CourseBriefDTO courseBriefDTO = new CourseBriefDTO(course, sectionCount, videoCount, avgRate);
                    if (course.getThumbnail() != null){
                        courseBriefDTO.setImage(viewImageFromS3(course.getThumbnail()));
                    }

                    return courseBriefDTO;
                });
        return result;
    }
}
