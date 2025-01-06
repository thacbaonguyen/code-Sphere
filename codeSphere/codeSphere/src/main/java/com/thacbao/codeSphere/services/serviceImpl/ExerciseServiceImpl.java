package com.thacbao.codeSphere.services.serviceImpl;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.dao.ExerciseDao;
import com.thacbao.codeSphere.dto.request.ExerciseRequest;
import com.thacbao.codeSphere.dto.request.ExerciseUpdateRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.CodeSphereResponse;
import com.thacbao.codeSphere.dto.response.ExerciseDTO;
import com.thacbao.codeSphere.entity.Subject;
import com.thacbao.codeSphere.entity.Exercise;
import com.thacbao.codeSphere.exceptions.NotFoundException;
import com.thacbao.codeSphere.repositories.SubjectRepository;
import com.thacbao.codeSphere.repositories.ExerciseRepository;
import com.thacbao.codeSphere.services.ExerciseService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.rmi.AlreadyBoundException;
import java.sql.SQLDataException;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ExerciseServiceImpl implements ExerciseService {
    private final ExerciseRepository exerciseRepository;

    private final JwtFilter jwtFilter;

    private final ModelMapper modelMapper;

    private final ExerciseDao exerciseDao;

    private final SubjectRepository subjectRepository;
    @Override
    public ResponseEntity<ApiResponse> insertExercise(ExerciseRequest request) {
        try{
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
                return CodeSphereResponse.generateResponse(new ApiResponse
                        ("success", "Insert exercise successfully", null), HttpStatus.OK);
            }
            else{
                return CodeSphereResponse.generateResponse(new ApiResponse
                        ("error", CodeSphereConstants.PERMISSION_DENIED, null), HttpStatus.FORBIDDEN);
            }
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> viewExerciseDetails(String code) {
        try{
            ExerciseDTO exerciseDTO = exerciseDao.viewExerciseDetails(code);
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("success", "Exercise details successfully", exerciseDTO), HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> filterExerciseBySubjectAndParam(Map<String, String> request, String order, String by, String search, Integer page) {
        try {
            List<ExerciseDTO> exerciseDTOS = exerciseDao.filterExerciseBySubjectAndParam(request.get("subject"), order, by, search, page);
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("success", "Exercise search successfully", exerciseDTOS), HttpStatus.OK);
        }
        catch (SQLDataException ex){
            return CodeSphereResponse.generateResponse(new ApiResponse("error", ex.getMessage(), null), HttpStatus.NOT_FOUND);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> activateExercise(Map<String, String> request) {
        Exercise exercise = exerciseRepository.findByCode(request.get("code"));
        if(exercise == null){
            throw new NotFoundException("Exercise not found");
        }
        try{
            exerciseDao.activateExercise(request.get("code"), Boolean.valueOf(request.get("isActive")));
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("success", "Activate exercise successfully", null), HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> updateExercise(ExerciseUpdateRequest request) {
        Exercise exercise = exerciseRepository.findByCode(request.getCode());
        if(exercise == null){
            throw new NotFoundException("Exercise not found");
        }
        try {
            exerciseDao.updateExercise(request);
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("success", "Update exercise successfully", null), HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> deleteExercise(String code) {
        Exercise exercise = exerciseRepository.findByCode(code);
        if(exercise == null){
            throw new NotFoundException("Exercise not found");
        }
        try{
            exerciseDao.deleteExercise(code);
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("success", "Delete exercise successfully", null), HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponse.generateResponse(new ApiResponse
                    ("error", ex.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
