package com.thacbao.codeSphere.controllers;

import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.SolutionStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/exercise/solution-storage")
public class SolutionStorageController {

    @Autowired
    private SolutionStorageService storageService;

    @PostMapping("/upload/{code}")
    public ResponseEntity<ApiResponse> upload(@PathVariable("code") String code, @RequestParam("file") MultipartFile file) {
        return storageService.uploadFile(file, code);
    }

    @GetMapping("/view")
    public ResponseEntity<ApiResponse> view(@RequestParam String filename) {
        return storageService.viewFile(filename);
    }

    @GetMapping("/all/{code}")
    public ResponseEntity<ApiResponse> all(@PathVariable String code) {
        return storageService.getAllToList(code);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id) {
        return storageService.deleteFile(id);
    }

}
