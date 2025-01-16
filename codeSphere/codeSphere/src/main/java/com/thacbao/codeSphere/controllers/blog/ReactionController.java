package com.thacbao.codeSphere.controllers.blog;

import com.thacbao.codeSphere.dto.request.blog.ReactionReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.ReactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reaction")
@RequiredArgsConstructor
public class ReactionController {

    private final ReactionService reactionService;
    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insertReaction(@RequestBody ReactionReq request) {

        return reactionService.insertReaction(request);
    }

    @PutMapping("/update")
    public ResponseEntity<ApiResponse> updateTypeReaction(@RequestBody ReactionReq request) {

        return reactionService.updateTypeReaction(request);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse> deleteReaction(@RequestBody ReactionReq request) {

        return reactionService.deleteReaction(request);
    }
}
