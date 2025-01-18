package com.thacbao.codeSphere.services.exerciseImpl;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.data.dao.ExerciseDao;
import com.thacbao.codeSphere.dto.request.exercise.ExerciseReq;
import com.thacbao.codeSphere.dto.request.exercise.ExerciseUdReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.exercise.ExerciseDTO;
import com.thacbao.codeSphere.entities.reference.Subject;
import com.thacbao.codeSphere.entities.core.Exercise;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.data.repository.exercise.SubjectRepository;
import com.thacbao.codeSphere.data.repository.exercise.ExerciseRepository;
import com.thacbao.codeSphere.services.ExerciseService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.rmi.AlreadyBoundException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static com.thacbao.codeSphere.constants.CodeSphereConstants.Exercise.EXERCISE_NOT_FOUND;
@Service
@RequiredArgsConstructor
@Slf4j
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;

    private final JwtFilter jwtFilter;

    private final ModelMapper modelMapper;

    private final ExerciseDao exerciseDao;

    private final SubjectRepository subjectRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * Tạo bài tập mới với role admin và manager
     * @param request
     * @return
     * @throws AlreadyBoundException
     */
    @Override
    public ResponseEntity<ApiResponse> insertExercise(ExerciseReq request) throws AlreadyBoundException {
        if(jwtFilter.isAdmin() || jwtFilter.isManager()){
            Subject subject = subjectRepository.findById(request.getSubjectId()).orElseThrow(
                    () -> new NotFoundException("Category not found")
            );
            Exercise exercise = exerciseRepository.findByCode(request.getCode());
            if(exercise != null){
                throw new AlreadyBoundException("Exercise already exists");
            }
            Exercise newExercise = modelMapper.map(request, Exercise.class);
            newExercise.setIsActive(true);
            newExercise.setCreatedBy(jwtFilter.getCurrentUsername());
            newExercise.setCreatedAt(LocalDate.now());
            exerciseRepository.save(newExercise);
            clearCache("exerciseFilter:"); // clear cache lay tat ca ex
            return CodeSphereResponses.generateResponse(null, "Insert exercise successfully", HttpStatus.OK);
        }
        else{
            return CodeSphereResponses.generateResponse(null, CodeSphereConstants.PERMISSION_DENIED, HttpStatus.FORBIDDEN);
        }
    }

    /**
     * Xem bài tập cụ thể
     * cache
     * @param code
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> viewExerciseDetails(String code) {
        String cacheKey = "exerciseDetails:" + code;
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        try{
            ExerciseDTO cacheExercise = (ExerciseDTO) valueOperations.get(cacheKey);
            if(cacheExercise != null){
                System.out.println("cache ex details" + cacheKey);
                return CodeSphereResponses.generateResponse(cacheExercise, "Exercise details successfully", HttpStatus.OK);
            }
            ExerciseDTO exerciseDTO = exerciseDao.viewExerciseDetails(code);
            valueOperations.set(cacheKey, exerciseDTO, 24, TimeUnit.HOURS);
            return CodeSphereResponses.generateResponse(exerciseDTO, "Exercise details successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Tim kiếm danh sách bài tập theo môn học
     * cache
     * @param request
     * @param order
     * @param by
     * @param search
     * @param page
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> filterExerciseBySubjectAndParam(Map<String, String> request, String order, String by, String search, Integer page) {
        String cacheKey = "exerciseFilter:" + request.get("subject") +
                (order != null && by != null ? order + ":" + by +":" : "") +
                (search != null ? search + ":" : "") + page;
        ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
        try {
            List<ExerciseDTO> cacheExercises = (List<ExerciseDTO>) valueOperations.get(cacheKey);
            if(cacheExercises != null){
                System.out.println("cache ex: " + cacheKey);
                return CodeSphereResponses.generateResponse(cacheExercises, "Filter exercises successfully", HttpStatus.OK);
            }
            List<ExerciseDTO> exerciseDTOS = exerciseDao.filterExerciseBySubjectAndParam(request.get("subject"), order, by, search, page);
            valueOperations.set(cacheKey, exerciseDTOS, 5, TimeUnit.HOURS);
            return CodeSphereResponses.generateResponse(exerciseDTOS, "Exercise search successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Active hoặc deactive bài tập
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> activateExercise(Map<String, String> request) {
        Exercise exercise = exerciseRepository.findByCode(request.get("code"));
        if(exercise == null){
            throw new NotFoundException(EXERCISE_NOT_FOUND);
        }
        try{
            exerciseDao.activateExercise(request.get("code"), Boolean.valueOf(request.get("isActive")));
            clearCache("exerciseFilter:" + exercise.getSubject().getName()); // clear cache voi mon hoc
            clearCache("exerciseDetails:" +request.get("code")); // clear cache view detail
            return CodeSphereResponses.generateResponse(null, "Activate exercise successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Chỉnh sửa bài tập
     * clear cache details, cache all
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> updateExercise(ExerciseUdReq request) {
        Exercise exercise = exerciseRepository.findByCode(request.getCode());
        if(exercise == null){
            throw new NotFoundException(EXERCISE_NOT_FOUND);
        }
        try {
            exerciseDao.updateExercise(request);
            clearCache("exerciseFilter:" + exercise.getSubject().getName());
            clearCache("exerciseDetails:" +exercise.getCode());
            return CodeSphereResponses.generateResponse(null, "Update exercise successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * xóa bài tập
     * @param code
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> deleteExercise(String code) {
        Exercise exercise = exerciseRepository.findByCode(code);
        if(exercise == null){
            throw new NotFoundException(EXERCISE_NOT_FOUND);
        }
        try{
            exerciseDao.deleteExercise(code);
            clearCache("exerciseFilter:" + exercise.getSubject().getName());
            clearCache("exerciseDetails:" +exercise.getCode());
            return CodeSphereResponses.generateResponse(null, "Delete exercise successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void clearCache(String cacheKey) {
        System.out.println("Clearing cache " + cacheKey);
        redisTemplate.delete(redisTemplate.keys(cacheKey + "*"));
    }
}
