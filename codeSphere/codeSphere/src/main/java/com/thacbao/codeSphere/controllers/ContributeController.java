package com.thacbao.codeSphere.controllers;

import com.thacbao.codeSphere.dto.request.ContributeReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.ContributeService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/contribute")
public class ContributeController {
    @Autowired
    private ContributeService contributeService;

    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendContribute(@RequestBody ContributeReq request){
        try{
            return contributeService.sendContribute(request);
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
