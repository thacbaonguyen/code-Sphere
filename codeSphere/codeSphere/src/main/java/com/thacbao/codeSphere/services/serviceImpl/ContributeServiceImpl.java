package com.thacbao.codeSphere.services.serviceImpl;

import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.dao.ContributeDao;
import com.thacbao.codeSphere.dto.request.ContributeReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.ContributeService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContributeServiceImpl implements ContributeService {
    private final ContributeDao contributeDao;

    private final CustomUserDetailsService customUserDetailsService;
    @Override
    public ResponseEntity<ApiResponse> sendContribute(ContributeReq request) {
        try{
            String username = customUserDetailsService.getUserDetails().getUsername();
            Integer userId = customUserDetailsService.getUserDetails().getId();
            contributeDao.save(request, username, userId);
            return CodeSphereResponses.generateResponse(null, "Send contribute successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
