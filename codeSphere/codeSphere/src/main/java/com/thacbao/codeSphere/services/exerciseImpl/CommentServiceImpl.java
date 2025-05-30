package com.thacbao.codeSphere.services.exerciseImpl;

import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.data.dao.CmtExDao;
import com.thacbao.codeSphere.dto.request.exercise.CmExReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.exercise.CmExHistoryDTO;
import com.thacbao.codeSphere.dto.response.exercise.CommentExDTO;
import com.thacbao.codeSphere.entities.reference.CmExHistory;
import com.thacbao.codeSphere.entities.reference.CommentExercise;
import com.thacbao.codeSphere.entities.core.Exercise;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.exceptions.user.PermissionException;
import com.thacbao.codeSphere.data.repository.exercise.CmExHistoryRepository;
import com.thacbao.codeSphere.data.repository.exercise.CmExRepository;
import com.thacbao.codeSphere.data.repository.exercise.ExerciseRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.services.CommentService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.thacbao.codeSphere.constants.CodeSphereConstants.PERMISSION_DENIED;
@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CmExRepository cmExRepository;

    private final JwtFilter jwtFilter;

    private final ExerciseRepository exerciseRepository;

    private final CmtExDao commentExDao;

    private final CmExHistoryRepository cmExHistoryRepository;

    private final RedisTemplate<String, Object> redisTemplate;
    private final CustomUserDetailsService userDetailsService;

    /**
     * Tạo mới comment cho bài tập
     * clear cache details exercise
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> insertComment(CmExReq request) {
        Exercise exercise = exerciseRepository.findByCode(request.getCode());
        User user = userDetailsService.getUserDetails();
        try {
            CommentExercise commentExercise = CommentExercise.builder()
                    .exercise(exercise)
                    .user(user)
                    .authorName(jwtFilter.getCurrentUsername())
                    .content(request.getContent())
                    .createdAt(LocalDate.now())
                    .updatedAt(LocalDate.now())
                    .build();
            commentExDao.save(commentExercise);
            clearCache("exerciseDetails:" + exercise.getCode()); // khi them moi cmt, xoa cache bai tap
            return CodeSphereResponses.generateResponse(null, "Insert comment success", HttpStatus.OK);
        }
        catch (Exception ex) {
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Show danh sách comment cho bài tập
     * @param code
     * @return
     * @throws SQLDataException
     */
    @Override
    public ResponseEntity<ApiResponse> getCommentEx(String code) throws SQLDataException {

        List<CommentExDTO> commentExDTOS = commentExDao.getCommentEx(code);
        return CodeSphereResponses.generateResponse(commentExDTOS, "Comment ex success", HttpStatus.OK);

    }

    /**
     * chỉnh sửa comment
     * lưu bản gốc vào history comment
     * @param request
     * @return
     * @throws SQLDataException
     */
    @Override
    public ResponseEntity<ApiResponse> updateComment(Map<String, String> request) throws SQLDataException {
        CommentExercise commentExercise = cmExRepository.findById(Integer.parseInt(request.get("id"))).orElseThrow(
                () -> new NotFoundException("Cannot found this comment")
        );
        if(jwtFilter.getCurrentUsername().equals(commentExercise.getAuthorName())){
            commentExDao.updateCommentEx(request.get("content"), Integer.parseInt(request.get("id")));
            CmExHistory cmExHistory = new CmExHistory();
            cmExHistory.setContent(commentExercise.getContent());
            cmExHistory.setUpdatedAt(LocalDateTime.now());
            cmExHistory.setCommentExercise(commentExercise);
            cmExHistoryRepository.save(cmExHistory);
            return CodeSphereResponses.generateResponse(null, "Update comment success", HttpStatus.OK);
        }
        else{
            throw new PermissionException(PERMISSION_DENIED);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getCommentHistory(Integer commentExerciseId) throws SQLDataException {

            List<CmExHistoryDTO> cmExHistoryDTOS = commentExDao.getCmExHistory(commentExerciseId);
            return CodeSphereResponses.generateResponse(cmExHistoryDTOS, "Comment history success", HttpStatus.OK);

    }

    private void clearCache(String cacheKey) {
        System.out.println("Clearing cache " + cacheKey);
        redisTemplate.delete(redisTemplate.keys(cacheKey + "*"));
    }
}
