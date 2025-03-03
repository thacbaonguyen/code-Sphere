package com.thacbao.codeSphere.services.exerciseImpl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.Api;
import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.configurations.JwtFilter;
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
import java.util.HashMap;
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

    /**
     * Gửi yêu cầu đóng góp bài tập
     * @param request
     * @return
     */
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
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Lấy tất cả các yêu cầu đóng góp với role admin và manager, Object mapper để cache
     * @param status
     * @param order, by, page
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> getAllContributeActive(Boolean status, String order, String by, Integer page) {
        String cacheKey = "allContribute:" + jwtFilter.getCurrentUsername() + status + (by != null ? by : "")
                          + (order != null ? order : "") + page;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (jwtFilter.isAdmin() || jwtFilter.isManager()) {
                // Kiểm tra cache
                String cachedData = (String) ops.get(cacheKey);
                if (cachedData != null) {
                    log.info("cache all {}", cacheKey);
                    // Chuyển đổi dữ liệu từ cache về đối tượng mới
                    Map<String, Object> responseMap = objectMapper.readValue(cachedData, Map.class);
                    return CodeSphereResponses.generateResponse(responseMap, "All contribute successfully", HttpStatus.OK);
                }

                // data from dao
                List<ContributeDTO> contributeList = contributeDao.getAllContributeActive(status, order, by, page);

                // build new response
                Map<String, Object> responseData = new HashMap<>();

                int currentPage = page;
                int pageSize = contributeList.size();
                Long totalElements = contributeDao.getAllRecord(status).longValue();
                int totalPages = (int) Math.ceil((double) totalElements / 20);

                // new response
                responseData.put("content", contributeList);
                responseData.put("page", currentPage);
                responseData.put("totalElement", totalElements);
                responseData.put("pageSize", pageSize);
                responseData.put("totalPages", totalPages);

                // parse json sang string
                String jsonData = objectMapper.writeValueAsString(responseData);
                ops.set(cacheKey, jsonData, 24, TimeUnit.HOURS);
                return CodeSphereResponses.generateResponse(responseData, "All contribute active successfully", HttpStatus.OK);
            } else {
                String cachedData = (String) ops.get(cacheKey);
                if (cachedData != null) {
                    log.info("cache all {}", cacheKey);
                    // Chuyển đổi dữ liệu từ cache về đối tượng mới
                    Map<String, Object> responseMap = objectMapper.readValue(cachedData, Map.class);
                    return CodeSphereResponses.generateResponse(responseMap, "All contribute successfully", HttpStatus.OK);
                }
                // Lấy dữ liệu từ DAO
                List<ContributeDTO> myContribute = contributeDao.getMyContribute(status, order, jwtFilter.getCurrentUsername());

                // Xây dựng response theo cấu trúc mới
                Map<String, Object> responseData = new HashMap<>();

                // Thiết lập thông tin phân trang
                int currentPage = 0; // Giả sử trang hiện tại
                int pageSize = myContribute.size(); // Hoặc kích thước trang thực tế
                long totalElements = myContribute.size(); // Tổng số bản ghi
                int totalPages = (int) Math.ceil((double) totalElements / pageSize); // Tổng số trang

                // Tạo cấu trúc response mới
                responseData.put("content", myContribute);
                responseData.put("page", currentPage);
                responseData.put("totalElement", totalElements);
                responseData.put("pageSize", pageSize);
                responseData.put("totalPages", totalPages);

                return CodeSphereResponses.generateResponse(responseData, "All contribute successfully", HttpStatus.OK);
            }
        } catch (Exception ex) {
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    /**
     * Xem chi tiết yêu cầu đóng góp
     * Admin và manager có thể xem all
     * user chỉ xem được của bản thân
     * @param id
     * @return
     * @throws SQLDataException
     */
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

    /**
     * Accept or reject yêu cầu với role admin and manager
     * @param request
     * @return
     * @throws SQLDataException
     */
    @Override
    public ResponseEntity<ApiResponse> activateContribute(Map<String, String> request) throws SQLDataException {

            if (jwtFilter.isAdmin() || jwtFilter.isManager()){
                contributeDao.activateContribute(request.get("id"), Boolean.valueOf(request.get("status")));
                clearCache("allContribute:");
                return CodeSphereResponses.generateResponse(null, "Activate contribute successfully", HttpStatus.OK);
            }
            throw new PermissionException(PERMISSION_DENIED);
    }

    /**
     * Sửa yêu cầu
     * @param request
     * @param id
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> updateContribute(ContributeReq request, Integer id) {
        try{
            contributeDao.updateContribute(request.getTitle(), request.getPaper(),
                    request.getInput(), request.getOutput(), request.getNote(), id);
            return CodeSphereResponses.generateResponse(null, "Update contribute successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
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
