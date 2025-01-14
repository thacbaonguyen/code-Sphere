package com.thacbao.codeSphere.controllers.exercise;

import com.thacbao.codeSphere.dto.request.exercise.ExerciseReq;
import com.thacbao.codeSphere.dto.request.exercise.ExerciseUdReq;
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

//    @PostMapping("/fake-data")
//    public ResponseEntity<ApiResponse> fakeData() throws AlreadyBoundException {
//        Faker faker = new Faker();
//        for (int i  = 0; i < 800000; i ++){
//            String code = generateCode("J");
//            if (exerciseRepository.findByCode(code) != null){
//                continue;
//            }
//            ExerciseReq req = new ExerciseReq();
//            req.setCode(code);
//            req.setTitle(faker.book().title());
//            req.setPaper(faker.lorem().sentence());
//            req.setInput(faker.lorem().word());
//            req.setOutput(faker.lorem().word());
//            req.setNote(faker.lorem().word());
//            req.setSubjectId(2);
//            req.setLevel(faker.number().numberBetween(1, 3));
//            req.setDescription(faker.leagueOfLegends().champion());
//            req.setTimeLimit(faker.number().numberBetween(1, 5));
//            req.setMemoryLimit(faker.number().numberBetween(65536, 200000));
//            req.setTopic(faker.book().genre());
//            exerciseService.insertExercise(req);
//        }
//        return CodeSphereResponses.generateResponse(null, "ok", HttpStatus.OK);
//    }
//
//    private String generateCode(String x) {
//        Random random = new Random();
//        return String.format(x + "%05d", random.nextInt(99999));
//    }
}
