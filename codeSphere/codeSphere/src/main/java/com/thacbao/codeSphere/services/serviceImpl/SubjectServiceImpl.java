package com.thacbao.codeSphere.services.serviceImpl;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.SubjectDTO;
import com.thacbao.codeSphere.entity.Subject;
import com.thacbao.codeSphere.exceptions.AlreadyException;
import com.thacbao.codeSphere.exceptions.PermissionException;
import com.thacbao.codeSphere.repositories.SubjectRepository;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SubjectServiceImpl {
    private final SubjectRepository subjectRepository;

    private final JwtFilter jwtFilter;

    public ResponseEntity<ApiResponse> insertNewSubject(Map<String, String> request){
        if(jwtFilter.isAdmin() || jwtFilter.isManager()){
            Subject subject = subjectRepository.findByName(request.get("name"));
            if(subject != null){
                throw new AlreadyException("Subject already exist");
            }
            Subject newSubject = new Subject();
            newSubject.setName(request.get("name"));
            subjectRepository.save(newSubject);
            return CodeSphereResponses.generateResponse(null, "Insert subject success", HttpStatus.OK);
        }
        else {
            throw new PermissionException("You do not have permission to add new subject");
        }
    }

    public ResponseEntity<ApiResponse> getAll(){
        try {
            List<Subject> subjects = subjectRepository.findAll();
            List<SubjectDTO> subjectDTOS = subjects.stream().map(item -> {
                SubjectDTO subjectDTO = new SubjectDTO();
                subjectDTO.setName(item.getName());
                return subjectDTO;
            }).collect(Collectors.toList());
            return CodeSphereResponses.generateResponse(subjectDTOS, "All subjects success", HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, "Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
