package com.thacbao.codeSphere.services.courseImpl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.data.repository.course.*;
import com.thacbao.codeSphere.data.specification.CourseSpecification;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.course.CourseBriefDTO;
import com.thacbao.codeSphere.dto.response.course.CourseDTO;
import com.thacbao.codeSphere.dto.response.course.CourseReviewDTO;
import com.thacbao.codeSphere.dto.response.course.SectionDTO;
import com.thacbao.codeSphere.entities.core.Course;
import com.thacbao.codeSphere.entities.reference.Section;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.services.CourseReviewService;
import com.thacbao.codeSphere.services.SectionService;
import com.thacbao.codeSphere.services.redis.RedisService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class CourseAccessService {
    private final CourseRepository courseRepository;
    private final OrderRepository orderRepository;
    private final CourseReviewService courseReviewService;
    private final SectionService sectionService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;
    private final AmazonS3 amazonS3;
    private final RedisService redisService;
    @Value("${cloud.aws.s3.bucketFeature}")
    private String bucketFeature;

    public ResponseEntity<ApiResponse> myCourse(){
        String cacheKey = "myCourses:" + jwtFilter.getCurrentUsername();
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String cachedData = (String) ops.get(cacheKey);
            if (cachedData != null) {
                log.info("cache all {}", cacheKey);
                Map<String, Object> responseMap = objectMapper.readValue(cachedData, Map.class);
                return CodeSphereResponses.generateResponse(responseMap, "All courses successfully", HttpStatus.OK);
            }
            Pageable pageable = createPageable(1, 100, "asc", "title");
            // Create specificationString
            Specification<Course> spec = Specification.where(CourseSpecification.hasPaid(jwtFilter.getCurrentUsername()));

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

    public ResponseEntity<ApiResponse> getCourseById(Integer id) {
        String cacheKey = "courseDetails:" + jwtFilter.getCurrentUsername() + (id > 0 ? id : "");
        try {
            CourseDTO courseCache = (CourseDTO) redisService.get(cacheKey);
            if (courseCache != null) {
                return CodeSphereResponses.generateResponse(courseCache, "View course successfully", HttpStatus.OK);
            }
            CourseDTO courseDTO = getCourseDTO(id);
            if (courseDTO.getThumbnail() != null) {
                courseDTO.setImage(viewImageFromS3(courseDTO.getThumbnail()));
            }
            redisService.set(cacheKey, courseDTO, 24, TimeUnit.HOURS);
            return CodeSphereResponses.generateResponse(courseDTO, "View course successfully", HttpStatus.OK);
        }
        catch (Exception e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
            return CodeSphereResponses.generateResponse(null, "View course failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    public boolean isAlreadyCourse(Integer courseId){
        return orderRepository.checkExistsByCourseId(courseId, userDetailsService.getUserDetails().getId());
    }

    private CourseDTO getCourseDTO(Integer id) {
        try {
            Course course = courseRepository.findByIdAndUserId(id, userDetailsService.getUserDetails().getId());
            List<CourseReviewDTO> courseReviewDTOS = courseReviewService.getCourseReviews(course.getId());
            List<SectionDTO> sectionDTOS = sectionService.getAllSection(course.getId());
            return new CourseDTO(course, courseReviewDTOS, sectionDTOS);
        }
        catch (Exception e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
            throw new NotFoundException("Not found your course");
        }

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
        return courseRepository.findAll(spec, pageable)
                .map(course -> {
                    int videoCount = 0;
                    for (Section item : course.getSections()){
                        videoCount+= item.getVideos().size();
                    }
                    CourseBriefDTO courseBriefDTO = new CourseBriefDTO(course, course.getSections().size(), videoCount);
                    if (course.getThumbnail() != null){
                        courseBriefDTO.setImage(viewImageFromS3(course.getThumbnail()));
                    }

                    return courseBriefDTO;
                });
    }

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
}
