package com.thacbao.codeSphere.services.courseImpl;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.data.repository.course.CourseReviewRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.dto.request.course.CourseReview;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.course.CourseReviewDTO;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.services.CourseReviewService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class CourseReviewServiceImpl implements CourseReviewService {
    private final CourseReviewRepository courseReviewRepository;
    private final ModelMapper modelMapper;
    private final UserRepository userRepository;
    private final JwtFilter jwtFilter;
    @Override
    public ResponseEntity<ApiResponse> createCourseReview(CourseReview request) {
        User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                ()-> new NotFoundException("Cannot found t his user")
        );

        com.thacbao.codeSphere.entities.reference.CourseReview courseReview = modelMapper.map(request, com.thacbao.codeSphere.entities.reference.CourseReview.class);
        courseReview.setCreatedAt(LocalDateTime.now());
        courseReview.setUser(user);
        courseReviewRepository.save(courseReview);
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
                    courseReviewDTO.setCreatedAt(review.getCreatedAt());
                    courseReviewDTO.setAuthor(review.getUser().getUsername());
                    return courseReviewDTO;
                }).toList();
    }
}
