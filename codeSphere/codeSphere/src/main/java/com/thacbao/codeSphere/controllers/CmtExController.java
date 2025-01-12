package com.thacbao.codeSphere.controllers;

import com.thacbao.codeSphere.dto.request.exercise.CmExReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.CommentService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/comment-ex")
@RequiredArgsConstructor
public class CmtExController {

    private final CommentService commentService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insertCommentEx(@Valid @RequestBody CmExReq request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return commentService.insertComment(request);
    }

    @GetMapping("/comment/{code}")
    public ResponseEntity<ApiResponse> getCommentEx(@PathVariable(name = "code") String code) throws SQLDataException {

        return commentService.getCommentEx(code);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateComment(@RequestBody Map<String, String> request) throws SQLDataException {

        return commentService.updateComment(request);
    }

    @GetMapping("/cmt-history/{comment_exercise_id}")
    public ResponseEntity<ApiResponse> getCommentHistory(@PathVariable(name = "comment_exercise_id") Integer commentExerciseId) throws SQLDataException {

        return commentService.getCommentHistory(commentExerciseId);

    }
}
