package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.blog.ReactionReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import org.springframework.http.ResponseEntity;

public interface ReactionService {
    ResponseEntity<ApiResponse> insertReaction(ReactionReq request);

    ResponseEntity<ApiResponse> updateTypeReaction(ReactionReq request);

    ResponseEntity<ApiResponse> deleteReaction(ReactionReq request);
}
