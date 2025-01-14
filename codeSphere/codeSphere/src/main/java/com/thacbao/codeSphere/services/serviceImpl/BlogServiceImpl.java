package com.thacbao.codeSphere.services.serviceImpl;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.data.repository.BlogRepository;
import com.thacbao.codeSphere.data.repository.UserRepository;
import com.thacbao.codeSphere.dto.request.blog.BlogReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.blog.BlogBriefDTO;
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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.thacbao.codeSphere.constants.CodeSphereConstants.User.USER_NOT_FOUND;
import static com.thacbao.codeSphere.constants.CodeSphereConstants.PERMISSION_DENIED;
import com.thacbao.codeSphere.data.specification.BlogSpecification;

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

    /**
     *
     * @param slug: tìm kiếm theo slug
     * thực hiện caching -> đảm bảo view được tăng
     * @return
     */
    @Override
    @Transactional
    public ResponseEntity<ApiResponse> viewBlog(String slug){
        String cacheKey = "blog:" + slug;
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();

        BlogDTO blogCache = (BlogDTO) ops.get(cacheKey);
        if(blogCache != null){
            log.info("cache blog details {}", cacheKey);
            incrementViewCount(slug);
            return CodeSphereResponses.generateResponse(blogCache, "Blog view successfully", HttpStatus.OK);
        }

        // Tăng view count
        Blog blog = incrementViewCount(slug);
        BlogDTO result = new BlogDTO(blog);
        ops.set(cacheKey, result, 24, TimeUnit.HOURS);

        return CodeSphereResponses.generateResponse(result, "Blog view successfully", HttpStatus.OK);
    }

    /**
     * @param search: tìm theo từ khóa
     * @param isFeature: tìm theo các bài viết được ghim
     * @param order by: sắp xếp theo tùy chọn
    */
    @Override
    public ResponseEntity<ApiResponse> getAllBlogs(
            String search,
            String isFeature,
            Integer page,
            Integer pageSize,
            String order,
            String by) {
        // Create pageable
        try{

            Sort.Direction direction = order != null && order.equalsIgnoreCase("asc") ?
                    Sort.Direction.ASC : Sort.Direction.DESC;
            String sortBy = by != null && !by.isEmpty() ? by : "publishedAt";
            Pageable pageable = PageRequest.of(
                    page,
                    pageSize,
                    Sort.by(direction, sortBy)
            );

            // Create specification
            Specification<Blog> spec = Specification.where(BlogSpecification.hasStatus())
                    .and(BlogSpecification.hasSearchText(search))
                    .and(BlogSpecification.hasIsFeatured(isFeature));


            Page<BlogBriefDTO> result = blogRepository.findAll(spec, pageable)
                    .map(BlogBriefDTO::new);

            return CodeSphereResponses.generateResponse(result, "Blog view successfully", HttpStatus.OK);
        }
        catch (Exception e){
            log.error("logging error with message {}", e.getMessage());
            return CodeSphereResponses.generateResponse(null, e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * @param tagName: tìm theo tag và các tham số khác
     */
    @Override
    public ResponseEntity<ApiResponse> findAllByTags(String tagName, String isFeatured,
                                                     Integer page, Integer pageSize, String order, String by) {
        try{
            Sort.Direction direction = order != null && order.equalsIgnoreCase("asc")
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            String sortBy = by != null && !by.isEmpty() ? by : "publishedAt";
            Pageable pageable = PageRequest.of(
                    page != null ? page : 0,
                    pageSize != null ? pageSize : 10,
                    Sort.by(direction, sortBy)

            );
            Specification<Blog> spec = Specification.where(BlogSpecification.hasStatus())
                    .and(BlogSpecification.hasIsFeatured(isFeatured))
                    .and(BlogSpecification.hasTag(tagName));
            Page<BlogBriefDTO> result = blogRepository.findAll(spec, pageable)
                    .map(BlogBriefDTO::new);
            return CodeSphereResponses.generateResponse(result, "Blog view successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     *
     * @param status: tìm theo trạng thái (published, draft, archive)
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> findMyBlogs(String search, String status, String order, String by) {
        try {
            Sort.Direction direction = order != null && by !=null && order.equalsIgnoreCase("asc") ?
                    Sort.Direction.ASC : Sort.Direction.DESC;

            String sortBy = by != null && !by.isEmpty() ? by : "publishedAt";
            Pageable pageable = PageRequest.of(
                    0, 6, Sort.by(direction, sortBy)
            );
            Specification<Blog> spec = Specification.where(status != null ? BlogSpecification.hasMyStatus(status) : null)
                    .and(BlogSpecification.hasSearchText(search))
                    .and(BlogSpecification.hasAuthor(jwtFilter.getCurrentUsername()));
            Page<BlogBriefDTO> result = blogRepository.findAll(spec, pageable).map(BlogBriefDTO::new);
            return CodeSphereResponses.generateResponse(result, "Blog view successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * update blog
     * @param id
     * @param request
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> updateBlog(Integer id, BlogReq request) {
        Blog blog = blogRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Cannot find blog with id " + id)
        );
        try {
          Set<Tag> tags = tagService.getOrCreateTags(request.getTags());
          blog.setTitle(request.getTitle());
          blog.setContent(request.getContent());
          blog.setExcerpt(request.getExcerpt());
          blog.setFeaturedImage(request.getFeaturedImage());
          blog.setFeatured(Boolean.parseBoolean(request.getIsFeatured()));
          blog.setStatus(request.getStatus());
          blog.setTags(tags);
          blog.setUpdatedAt(LocalDate.now());
          blogRepository.save(blog);
          return CodeSphereResponses.generateResponse(null, "Blog update successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Admin có thể xóa bất kì bài viết nào
     * Blogger chỉ có thể xóa bài viết của chính mình
     * @param id
     * @return
     */
    @Override
    public ResponseEntity<ApiResponse> deleteBlog(Integer id) {
        Blog blog = blogRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Cannot find blog with id " + id)
        );
        if (jwtFilter.isAdmin()){
            blogRepository.delete(blog);
            return CodeSphereResponses.generateResponse(null, "Blog delete successfully", HttpStatus.OK);
        }
        if (jwtFilter.isBlogger()){
           if (blog.getAuthor().getUsername().equals(jwtFilter.getCurrentUsername())){
               blogRepository.delete(blog);
               return CodeSphereResponses.generateResponse(null, "Blog delete successfully", HttpStatus.OK);
           }

        }
        return CodeSphereResponses.generateResponse(null, "You do not have permission", HttpStatus.FORBIDDEN);
    }

    /**
     * @param slug: tăng view khi có lượt xem blogs
     * @return Blog
     */
    @Transactional
    public Blog incrementViewCount(String slug) {
        Blog blog = blogRepository.findBySlug(slug)
                .orElseThrow(() -> new NotFoundException("Blog not found with slug: " + slug));
        blog.setViewCount(blog.getViewCount() + 1);
        blogRepository.save(blog);
        return blog;
    }
}
