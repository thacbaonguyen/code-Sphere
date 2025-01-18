package com.thacbao.codeSphere.services.blogImpl;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.data.repository.blog.BlogRepository;
import com.thacbao.codeSphere.data.repository.blog.ReactionRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.dto.request.blog.ReactionReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.entities.core.Blog;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.Reaction;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.services.ReactionService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ReactionServiceImpl implements ReactionService {

    private final ReactionRepository reactionRepository;
    private final JwtFilter jwtFilter;
    private final UserRepository userRepository;
    private final BlogRepository blogRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    @Override
    public ResponseEntity<ApiResponse> insertReaction(ReactionReq request) {
        try{
            Blog blog = getBlog(request.getBlogId());
            Reaction reaction = Reaction.builder()
                    .user(getUser())
                    .blog(blog)
                    .reactionType(request.getReactionType())
                    .createdAt(LocalDateTime.now())
                    .build();
            reactionRepository.save(reaction);
            redisTemplate.delete(redisTemplate.keys("blog:" + blog.getSlug()));
            log.info("clear cache blog details {}", blog.getSlug());
            return CodeSphereResponses.generateResponse(null, "Insert reaction successfully", HttpStatus.CREATED);
        }
        catch (NotFoundException ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public ResponseEntity<ApiResponse> updateTypeReaction(ReactionReq request) {
        try {
            Reaction reaction = getReaction(request.getBlogId());
            reaction.setReactionType(request.getReactionType());
            reactionRepository.save(reaction);
            return CodeSphereResponses.generateResponse(null, "Update reaction successfully", HttpStatus.OK);
        }
        catch (NotFoundException ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.NOT_FOUND);
        }

    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> deleteReaction(ReactionReq request) {
        reactionRepository.deleteCustom( getUser().getId(), request.getBlogId());
        return CodeSphereResponses.generateResponse(null, "Delete reaction successfully", HttpStatus.OK);
    }

    private User getUser(){
        return userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                () -> new NotFoundException(CodeSphereConstants.User.USER_NOT_FOUND)
        );
    }

    private Blog getBlog(Integer id){
        return blogRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Blog not found")
        );
    }

    private Reaction getReaction(Integer blogId){
        return reactionRepository.findByUserAndBlog(getUser(), getBlog(blogId)).orElseThrow(
                () -> new NotFoundException("Reaction not found")
        );
    }
}
