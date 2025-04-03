package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.data.dao.ContributeDao;
import com.thacbao.codeSphere.data.repository.blog.BlogRepository;
import com.thacbao.codeSphere.data.repository.course.CourseRepository;
import com.thacbao.codeSphere.data.repository.exercise.ExerciseRepository;
import com.thacbao.codeSphere.data.repository.user.RegisterRoleRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.redis.RedisService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {
    private final UserRepository userRepository;

    private final ExerciseRepository exerciseRepository;

    private final BlogRepository blogRepository;

    private final JwtFilter jwtFilter;

    private final RegisterRoleRepository registerRoleRepository;

    private final ContributeDao contributeDao;
    private final CourseRepository courseRepository;
    private final RedisService redisService;

    // book
    // course

    public ResponseEntity<ApiResponse> overview(){
        String cacheKey = "overview";
        try {
            if (jwtFilter.isAdmin() || jwtFilter.isManager()){
                Map<String, Object> data = redisService.get(cacheKey);
                if (data != null) {
                    return CodeSphereResponses.generateResponse(data, "Overview successfully", HttpStatus.OK);
                }
                Map<String, Long> map = new HashMap<>();
                map.put("users", userRepository.count());
                map.put("exercises", exerciseRepository.count());
                map.put("blogs", blogRepository.count());
                map.put("registerRoles", registerRoleRepository.count());
                map.put("contributes", contributeDao.countContribute());
                //put book
                map.put("courses", courseRepository.count());
                redisService.set(cacheKey, map, 24, TimeUnit.HOURS);
                return CodeSphereResponses.generateResponse(map, "Overview successfully", HttpStatus.OK);
            }
            return CodeSphereResponses.generateResponse(null, CodeSphereConstants.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
        }
        catch (Exception e){
            log.error("Error over view dashboard {}", e.getMessage(), e.getCause());
            return CodeSphereResponses.generateResponse(null, "over view failed", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

}
