package com.thacbao.codeSphere.controllers.exercise;

import com.thacbao.codeSphere.dto.request.exercise.ContributeReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.ContributeService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.sql.SQLDataException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contribute")
@RequiredArgsConstructor
public class ContributeController {

    private final ContributeService contributeService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendContribute(@Valid @RequestBody ContributeReq request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return contributeService.sendContribute(request);
    }

    @GetMapping("/exercise")
    public ResponseEntity<ApiResponse> getAllContributeByStatus(@RequestParam(defaultValue = "true") String status,
                                                                @RequestParam(required = false) String order,
                                                                @RequestParam(required = false) String by,
                                                                @RequestParam(defaultValue = "1") Integer page
                                                                ){
            Boolean parseStatus = Boolean.parseBoolean(status);
            return contributeService.getAllContributeActive(parseStatus, order, by, page);
    }

    @GetMapping("/exercise/details/{id}")
    public ResponseEntity<ApiResponse> getContributeDetails(@PathVariable("id") Integer id) throws SQLDataException {
            return contributeService.getContributeDetails(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateContribute(@RequestBody ContributeReq request,
                                                        @PathVariable Integer id){
            return contributeService.updateContribute(request, id);
    }

    @PutMapping("/activate")
    public ResponseEntity<ApiResponse> activateContribute(@RequestBody Map<String, String> request) throws SQLDataException {
            return contributeService.activateContribute(request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> deleteContribute(@PathVariable Integer id) throws SQLDataException {
        return contributeService.deleteContribute(id);
    }

}
