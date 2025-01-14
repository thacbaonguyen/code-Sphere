package com.thacbao.codeSphere.controllers.exercise;

import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.serviceImpl.SubjectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/subject")
public class SubjectController {

    @Autowired
    private SubjectServiceImpl subjectService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insert(@RequestBody Map<String, String> request) {

        return subjectService.insertNewSubject(request);

    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse> getAllSubjects() {

        return subjectService.getAll();

    }
}
