package com.thacbao.codeSphere.services.serviceImpl;

import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.data.dao.ContributeDao;
import com.thacbao.codeSphere.dto.request.exercise.ContributeReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.exercise.ContributeDTO;
import com.thacbao.codeSphere.exceptions.user.PermissionException;
import com.thacbao.codeSphere.services.ContributeService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.sql.SQLDataException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.thacbao.codeSphere.constants.CodeSphereConstants.*;
@Service
@RequiredArgsConstructor
@Slf4j
public class ContributeServiceImpl implements ContributeService {
    private final ContributeDao contributeDao;

    private final CustomUserDetailsService customUserDetailsService;

    private final RedisTemplate<String, Object> redisTemplate;

    private final JwtFilter jwtFilter;
    @Override
    public ResponseEntity<ApiResponse> sendContribute(ContributeReq request) {
        try{
            String username = customUserDetailsService.getUserDetails().getUsername();
            Integer userId = customUserDetailsService.getUserDetails().getId();
            contributeDao.save(request, username, userId);
            clearCache("allContribute:");
            return CodeSphereResponses.generateResponse(null, "Send contribute successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getAllContributeActive(Boolean status, String dateOrder) {
        String cacheKey = "allContribute:status:" + jwtFilter.getCurrentUsername() + status + (dateOrder != null ? dateOrder : "");
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        try {
            if (jwtFilter.isAdmin() || jwtFilter.isManager()){
                List<ContributeDTO> cacheContribute  = (List<ContributeDTO>) ops.get(cacheKey);
                if (cacheContribute != null) {
                    log.info("cache all {}", cacheKey);
                    return CodeSphereResponses.generateResponse(cacheContribute, "All contribute successfully", HttpStatus.OK);
                }
                List<ContributeDTO> contributeList = contributeDao.getAllContributeActive(status, dateOrder);
                ops.set(cacheKey, contributeList, 24, TimeUnit.HOURS);
                return CodeSphereResponses.generateResponse(contributeList, "All contribute active successfully", HttpStatus.OK);
            }
            else{
                List<ContributeDTO> myContribute = contributeDao.getMyContribute(status, dateOrder, jwtFilter.getCurrentUsername());
                return CodeSphereResponses.generateResponse(myContribute, "All contribute successfully", HttpStatus.OK);
            }
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> getContributeDetails(Integer id) throws SQLDataException {
        ContributeDTO contribute = contributeDao.getContributeDetails(id);

        if(jwtFilter.isAdmin() || jwtFilter.isManager())
            return CodeSphereResponses.generateResponse(contribute, "Get contribute successfully", HttpStatus.OK);

        if(contribute.getAuthor().equals(jwtFilter.getCurrentUsername()))
            return CodeSphereResponses.generateResponse(contribute, "Get contribute successfully", HttpStatus.OK);

        else
            throw new PermissionException(PERMISSION_DENIED);
    }

    @Override
    public ResponseEntity<ApiResponse> activateContribute(Map<String, String> request) throws SQLDataException {

            if (jwtFilter.isAdmin() || jwtFilter.isManager()){
                contributeDao.activateContribute(request.get("id"), Boolean.valueOf(request.get("status")));
                clearCache("allContribute:");
                return CodeSphereResponses.generateResponse(null, "Activate contribute successfully", HttpStatus.OK);
            }
            throw new PermissionException(PERMISSION_DENIED);
    }

    @Override
    public ResponseEntity<ApiResponse> updateContribute(ContributeReq request, Integer id) {
        try{
            contributeDao.updateContribute(request.getTitle(), request.getPaper(),
                    request.getInput(), request.getOutput(), request.getNote(), id);
            clearCache("allContribute:");
            return CodeSphereResponses.generateResponse(null, "Update contribute successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> deleteContribute(Integer id) throws SQLDataException {
        if (jwtFilter.isAdmin() || jwtFilter.isManager()){
            contributeDao.deleteContribute(id);
            clearCache("allContribute:");
            return CodeSphereResponses.generateResponse(null, "Delete contribute successfully", HttpStatus.OK);
        }
        throw new PermissionException(PERMISSION_DENIED);
    }

    private void  clearCache(String cacheKey){
        log.info("clear cache {}", cacheKey);
        redisTemplate.delete(redisTemplate.keys(cacheKey + "*"));
    }
}
