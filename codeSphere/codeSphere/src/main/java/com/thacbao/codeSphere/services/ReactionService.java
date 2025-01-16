package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.dto.request.blog.ReactionReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.entity.reference.Reaction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

public interface ReactionService {
    ResponseEntity<ApiResponse> insertReaction(ReactionReq request);

    ResponseEntity<ApiResponse> updateTypeReaction(ReactionReq request);

    ResponseEntity<ApiResponse> deleteReaction(ReactionReq request);
}
