package com.thacbao.codeSphere.controllers;

import com.thacbao.codeSphere.dto.request.ExerciseReq;
import com.thacbao.codeSphere.dto.request.ExerciseUdReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.ExerciseService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.rmi.AlreadyBoundException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/exercise")
@RequiredArgsConstructor
public class ExerciseController {
    private final ExerciseService exerciseService;
    // tao moi bai tap
    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insertExercise(@Valid @RequestBody ExerciseReq request, BindingResult bindingResult) throws AlreadyBoundException {

        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return exerciseService.insertExercise(request);
    }
    //tim kiem bai tai theo mon va cac param
    @GetMapping("/subject/question")
    public ResponseEntity<ApiResponse> filterExerciseBySubject(@RequestBody Map<String, String> request,
                                                               @RequestParam(required = false) String order,
                                                               @RequestParam(required = false) String by,
                                                               @RequestParam(required = false) String search,
                                                               @RequestParam(defaultValue = "1") Integer page) {

        return exerciseService.filterExerciseBySubjectAndParam(request, order, by, search, page);

    }
    // xem chi tiet bai  tap cu the
    @GetMapping("/question/{code}")
    public ResponseEntity<ApiResponse> viewExerciseDetails(@PathVariable String code) {

        return exerciseService.viewExerciseDetails(code);

    }
    // sua doi trang thai cua bai tap
    @PutMapping("/active")
    public ResponseEntity<ApiResponse> activateExercise(@RequestBody Map<String, String> request) {

        return exerciseService.activateExercise(request);

    }
    //update
    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateExercise(@Valid @RequestBody ExerciseUdReq request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return exerciseService.updateExercise(request);
    }
    //xoa bai tap the code
    @DeleteMapping("/delete/{code}")
    public ResponseEntity<ApiResponse> deleteExercise(@PathVariable String code) {

        return exerciseService.deleteExercise(code);

    }
}
