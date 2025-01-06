package com.thacbao.codeSphere.services.serviceImpl;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.dao.CommentExDao;
import com.thacbao.codeSphere.dto.request.CmExRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.CmExHistoryDTO;
import com.thacbao.codeSphere.dto.response.CommentExDTO;
import com.thacbao.codeSphere.entity.CmExHistory;
import com.thacbao.codeSphere.entity.CommentExercise;
import com.thacbao.codeSphere.entity.Exercise;
import com.thacbao.codeSphere.entity.User;
import com.thacbao.codeSphere.exceptions.NotFoundException;
import com.thacbao.codeSphere.exceptions.PermissionException;
import com.thacbao.codeSphere.repositories.CmExHistoryRepository;
import com.thacbao.codeSphere.repositories.CmExRepository;
import com.thacbao.codeSphere.repositories.ExerciseRepository;
import com.thacbao.codeSphere.repositories.UserRepository;
import com.thacbao.codeSphere.services.CommentService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CmExRepository cmExRepository;

    private final JwtFilter jwtFilter;

    private final UserRepository userRepository;

    private final ExerciseRepository exerciseRepository;

    private final CommentExDao commentExDao;

    private final CmExHistoryRepository cmExHistoryRepository;

    @Override
    public ResponseEntity<ApiResponse> insertComment(CmExRequest request) {
        try {
            Exercise exercise = exerciseRepository.findById(request.getExerciseId()).orElseThrow(
                    ()-> new NotFoundException("Exercise not found")
            );
            User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                    ()-> new NotFoundException("User not found")
            );
            CommentExercise commentExercise = CommentExercise.builder()
                    .exercise(exercise)
                    .user(user)
                    .authorName(jwtFilter.getCurrentUsername())
                    .content(request.getContent())
                    .createdAt(LocalDate.now())
                    .updatedAt(LocalDate.now())
                    .build();
            cmExRepository.save(commentExercise);
            return CodeSphereResponses.generateResponse(null, "Insert comment success", HttpStatus.OK);
        }
        catch (Exception e) {
            return CodeSphereResponses.generateResponse(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getCommentEx(Integer exerciseId) {
        try{
            List<CommentExDTO> commentExDTOS = commentExDao.getCommentEx(exerciseId);
            return CodeSphereResponses.generateResponse(commentExDTOS, "Comment ex success", HttpStatus.OK);
        }
        catch (Exception e) {
            return CodeSphereResponses.generateResponse(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> updateComment(Map<String, String> request) {
        try {
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
                throw new PermissionException("You do not have permission to update this comment");
            }
        }
        catch (Exception e) {
            return CodeSphereResponses.generateResponse(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getCommentHistory(Integer commentExerciseId) {
        try {
            List<CmExHistoryDTO> cmExHistoryDTOS = commentExDao.getCmExHistory(commentExerciseId);
            return CodeSphereResponses.generateResponse(cmExHistoryDTOS, "Comment history success", HttpStatus.OK);
        }
        catch (Exception e) {
            return CodeSphereResponses.generateResponse(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
