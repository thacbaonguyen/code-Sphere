package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.ContributeReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface ContributeService {
    ResponseEntity<ApiResponse> sendContribute(ContributeReq request);
}
