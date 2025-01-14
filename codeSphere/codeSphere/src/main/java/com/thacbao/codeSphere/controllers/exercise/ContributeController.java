package com.thacbao.codeSphere.controllers.exercise;

import com.thacbao.codeSphere.dto.request.exercise.ContributeReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.ContributeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.sql.SQLDataException;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/contribute")
public class ContributeController {
    @Autowired
    private ContributeService contributeService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendContribute(@RequestBody ContributeReq request){
            return contributeService.sendContribute(request);
    }

    @GetMapping("/exercise")
    public ResponseEntity<ApiResponse> getAllContributeByStatus(@RequestParam(defaultValue = "true") String status,
                                                                @RequestParam(required = false) String dateOrder){
            Boolean parseStatus = Boolean.parseBoolean(status);
            return contributeService.getAllContributeActive(parseStatus, dateOrder);
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
