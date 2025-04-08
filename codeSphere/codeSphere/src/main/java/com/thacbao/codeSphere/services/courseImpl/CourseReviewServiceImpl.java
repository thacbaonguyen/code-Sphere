package com.thacbao.codeSphere.services.courseImpl;

import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.data.repository.course.CourseRepository;
import com.thacbao.codeSphere.data.repository.course.CourseReviewRepository;
import com.thacbao.codeSphere.dto.request.course.CourseReview;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.course.CourseReviewDTO;
import com.thacbao.codeSphere.entities.core.Course;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.exceptions.common.AlreadyException;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.services.CourseReviewService;
import com.thacbao.codeSphere.services.redis.RedisService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseReviewServiceImpl implements CourseReviewService {
    private final CourseReviewRepository courseReviewRepository;
    private final ModelMapper modelMapper;
    private final CustomUserDetailsService customUserDetailsService;
    private final CourseRepository courseRepository;
    private final RedisService redisService;

    @Override
    public ResponseEntity<ApiResponse> createCourseReview(CourseReview request) {
        User user = customUserDetailsService.getUserDetails();
        Course course = courseRepository.findById(request.getCourseId()).orElseThrow(
                () -> new NotFoundException("Course not found")
        );
        boolean existing = courseReviewRepository.exists(request.getCourseId(), user.getId());
        if (existing) {
            throw new AlreadyException("Your review already exists");
        }
        com.thacbao.codeSphere.entities.reference.CourseReview courseReview = modelMapper.map(request, com.thacbao.codeSphere.entities.reference.CourseReview.class);
        courseReview.setCreatedAt(LocalDateTime.now());
        courseReview.setUser(user);
        courseReview.setId(null);
        courseReviewRepository.save(courseReview);
        course.setTotalRate(course.getTotalRate() + 1);
        int rating = courseReview.getRating();
        float result = (rating + (course.getRate() * course.getTotalRate()))/(course.getTotalRate() + 1);
        course.setRate(result);
        courseRepository.save(course);
        redisService.delete("courseDetails:");
        return CodeSphereResponses.generateResponse(null, "Create rating success", HttpStatus.CREATED);
    }

    @Override
    public List<CourseReviewDTO> getCourseReviews(Integer courseId) {
        return courseReviewRepository.findByCourseId(courseId)
                .stream().map(review -> {
                    CourseReviewDTO courseReviewDTO = new CourseReviewDTO();
                    courseReviewDTO.setId(review.getId());
                    courseReviewDTO.setContent(review.getContent());
                    courseReviewDTO.setRating(review.getRating());
                    courseReviewDTO.setCreatedAt(review.getCreatedAt().toString());
                    courseReviewDTO.setAuthor(review.getUser().getUsername());
                    return courseReviewDTO;
                }).toList();
    }
}
