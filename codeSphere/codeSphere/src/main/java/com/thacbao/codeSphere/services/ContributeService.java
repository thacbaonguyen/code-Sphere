package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.exercise.ContributeReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

import java.sql.SQLDataException;
import java.util.Map;

public interface ContributeService {
    ResponseEntity<ApiResponse> sendContribute(ContributeReq request);

    ResponseEntity<ApiResponse> getAllContributeActive(Boolean status, String dateOrder);

    ResponseEntity<ApiResponse> getContributeDetails(Integer id) throws SQLDataException;

    ResponseEntity<ApiResponse> activateContribute(Map<String, String> request) throws SQLDataException;

    ResponseEntity<ApiResponse> updateContribute(ContributeReq request, Integer id);

    ResponseEntity<ApiResponse> deleteContribute(Integer id) throws SQLDataException;


}
