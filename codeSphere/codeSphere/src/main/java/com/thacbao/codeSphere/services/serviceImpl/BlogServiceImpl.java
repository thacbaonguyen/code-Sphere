package com.thacbao.codeSphere.services.serviceImpl;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.data.repository.BlogRepository;
import com.thacbao.codeSphere.data.repository.UserRepository;
import com.thacbao.codeSphere.dto.request.blog.BlogReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.blog.BlogDTO;
import com.thacbao.codeSphere.entity.core.Blog;
import com.thacbao.codeSphere.entity.core.User;
import com.thacbao.codeSphere.entity.reference.Tag;
import com.thacbao.codeSphere.exceptions.user.NotFoundException;
import com.thacbao.codeSphere.exceptions.user.PermissionException;
import com.thacbao.codeSphere.services.BlogService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.thacbao.codeSphere.constants.CodeSphereConstants.User.USER_NOT_FOUND;
import static com.thacbao.codeSphere.constants.CodeSphereConstants.PERMISSION_DENIED;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogServiceImpl implements BlogService {
    private final UserRepository userRepository;

    private final JwtFilter jwtFilter;

    private final TagService tagService;

    private final BlogRepository blogRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public ResponseEntity<ApiResponse> insertBlog(BlogReq request) {
        User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                ()-> new NotFoundException(USER_NOT_FOUND)
        );

        if (jwtFilter.isAdmin() || jwtFilter.isBlogger()){
            Set<Tag> tags = tagService.getOrCreateTags(request.getTags());
            Blog blog = request.toEntity(user, tags);
            blogRepository.save(blog);
            return CodeSphereResponses.generateResponse(null, "Blog create successfully", HttpStatus.CREATED);
        }
        else
            throw new PermissionException(PERMISSION_DENIED);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> viewBlog(String slug){
        String cacheKey = "blog:" + slug;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        BlogDTO blogCache = (BlogDTO) ops.get(cacheKey);
        if(blogCache != null){
            log.info("cache blog details {}", cacheKey);
            return CodeSphereResponses.generateResponse(blogCache, "Blog view successfully", HttpStatus.OK);
        }
        Blog blog = blogRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Blog not found with slug: " + slug));

        // Tăng view count
        incrementViewCount(blog);
        BlogDTO result = new BlogDTO(blog);
        ops.set(cacheKey, result, 24, TimeUnit.HOURS);

        return CodeSphereResponses.generateResponse(result, "Blog view successfully", HttpStatus.OK);
    }

    @Transactional
    public void incrementViewCount(Blog blog) {
        blog.setViewCount(blog.getViewCount() + 1);
        blogRepository.save(blog);
    }
}
