package com.thacbao.codeSphere.services.blogImpl;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.data.repository.blog.BlogRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.dto.request.blog.BlogReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.blog.BlogBriefDTO;
import com.thacbao.codeSphere.dto.response.blog.BlogDTO;
import com.thacbao.codeSphere.entities.core.Blog;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.Tag;
import com.thacbao.codeSphere.exceptions.common.AppException;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.exceptions.user.PermissionException;
import com.thacbao.codeSphere.services.BlogService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
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
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.thacbao.codeSphere.constants.CodeSphereConstants.User.USER_NOT_FOUND;
import static com.thacbao.codeSphere.constants.CodeSphereConstants.PERMISSION_DENIED;
import com.thacbao.codeSphere.data.specification.BlogSpecification;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlogServiceImpl implements BlogService {


    private final JwtFilter jwtFilter;

    private final TagService tagService;

    private final BlogRepository blogRepository;

    private final RedisTemplate<String, Object> redisTemplate;

    private final AmazonS3 amazonS3;

    private final CustomUserDetailsService userDetailsService;

    @Value("${cloud.aws.s3.bucketFeature}")
    private String bucketFeature;

    @Override
    public ResponseEntity<ApiResponse> insertBlog(BlogReq request) {
        User user = userDetailsService.getUserDetails();

        if (jwtFilter.isAdmin() || jwtFilter.isBlogger()){
            Set<Tag> tags = tagService.getOrCreateTags(request.getTags());
            Blog blog = request.toEntity(user, tags);
            blogRepository.save(blog);
            Map<String, Object> response = new HashMap<>();
            response.put("id", blog.getId());
            return CodeSphereResponses.generateResponse(response, "Blog create successfully", HttpStatus.CREATED);
        }
        else
            throw new PermissionException(PERMISSION_DENIED);
    }

    /**
     * Nếu ảnh chưa được set trước đó -> tạo mới
     * Nếu ảnh trước có tồn tại -> set ảnh mới and xóa ảnh cũ khỏi cloud
     * Thực hiện xóa cache nếu là action update
     * @param blogId
     * @param file
     */
    @Override
    public void uploadFeatureImage(Integer blogId, MultipartFile file) {
        Blog blog = blogRepository.findById(blogId).orElseThrow(
                () -> new NotFoundException("Can't find blog with id " + blogId)
        );
        try {
            if (validateFile(file)) {
                String oldFilename = blog.getFeaturedImage();
                String fileName = uploadToS3(file);
                blog.setFeaturedImage(fileName);
                blogRepository.save(blog);
                if (oldFilename != null) {
                    deleteFromS3(oldFilename);
                    redisTemplate.delete(redisTemplate.keys("blog:" + blog.getSlug()));
                    log.info("update action and clear cache blog:{}", blog.getSlug());
                }
            }
        }
        catch (AppException | IOException e) {
            log.error("logging error with message {}", e.getMessage(), e.getCause());
        }
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
        if (blog.getFeaturedImage() != null){
            result.setImage(viewImageFromS3(blog.getFeaturedImage()));
        }
        ops.set(cacheKey, result, 5, TimeUnit.HOURS);

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
            String by,
            String status) {
        // Create pageable
        try{

            Sort.Direction direction = order != null && order.equalsIgnoreCase("asc") ?
                    Sort.Direction.ASC : Sort.Direction.DESC;
            String sortBy = by != null && !by.isEmpty() ? by : "updatedAt";
            Pageable pageable = PageRequest.of(
                    page - 1,
                    pageSize,
                    Sort.by(direction, sortBy)
            );
            log.info("current page {}", page);
            log.info("current pageS {}", pageSize);
            // Create specification
            Specification<Blog> spec = Specification.where(BlogSpecification.hasStatus(status))
                    .and(BlogSpecification.hasSearchText(search))
                    .and(BlogSpecification.hasIsFeatured(isFeature));


            Page<BlogBriefDTO> result = blogRepository.findAll(spec, pageable)
                    .map(blog -> {
                        BlogBriefDTO blogBriefDTO = new BlogBriefDTO(blog);
                        if (blog.getFeaturedImage() != null){
                            blogBriefDTO.setImage(viewImageFromS3(blog.getFeaturedImage()));
                        }

                        return blogBriefDTO;
                    });

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
                                                     Integer page, Integer pageSize, String order, String by, String status) {
        try{
            Sort.Direction direction = order != null && order.equalsIgnoreCase("asc")
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            String sortBy = by != null && !by.isEmpty() ? by : "publishedAt";
            Pageable pageable = PageRequest.of(
                    page - 1,
                    pageSize != null ? pageSize : 15,
                    Sort.by(direction, sortBy)

            );
            Specification<Blog> spec = Specification.where(BlogSpecification.hasStatus(status))
                    .and(BlogSpecification.hasIsFeatured(isFeatured))
                    .and(BlogSpecification.hasTag(tagName));
            Page<BlogBriefDTO> result = blogRepository.findAll(spec, pageable)
                    .map(blog -> {
                        BlogBriefDTO blogBriefDTO = new BlogBriefDTO(blog);
                        if (blog.getFeaturedImage() != null){
                            blogBriefDTO.setImage(viewImageFromS3(blog.getFeaturedImage()));
                        }
                        return blogBriefDTO;
                    });
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
            Page<BlogBriefDTO> result = blogRepository.findAll(spec, pageable).map(
                    blog -> {
                        BlogBriefDTO blogBriefDTO = new BlogBriefDTO(blog);
                        if (blog.getFeaturedImage() != null){
                            blogBriefDTO.setImage(viewImageFromS3(blog.getFeaturedImage()));
                        }

                        return blogBriefDTO;
                    });
            return CodeSphereResponses.generateResponse(result, "Blog view successfully", HttpStatus.OK);
        }
        catch (Exception ex){
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * update blog
     * xóa cache
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
            if (blog.getAuthor().getUsername().equals(jwtFilter.getCurrentUsername())){
                Set<Tag> tags = tagService.getOrCreateTags(request.getTags());
                blog.setTitle(request.getTitle());
                blog.setContent(request.getContent());
                blog.setExcerpt(request.getExcerpt());
                blog.setFeatured(Boolean.parseBoolean(request.getIsFeatured()));
                blog.setStatus(request.getStatus());
                blog.setTags(tags);
                blog.setUpdatedAt(LocalDate.now());
                blogRepository.save(blog);
                redisTemplate.delete(redisTemplate.keys("blog:" + blog.getSlug()));
                log.info("clear cache blog:{}", blog.getSlug());
                return CodeSphereResponses.generateResponse(null, "Blog update successfully", HttpStatus.OK);
            }
            return CodeSphereResponses.generateResponse(null, "You do not have permission", HttpStatus.FORBIDDEN);
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
        if (jwtFilter.isAdmin() || jwtFilter.isManager()){
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

    /**
     * Upload file lên cloud
     * @param file
     * @return
     * @throws IOException
     */
    private String uploadToS3(MultipartFile file) throws IOException {
        String fileName = UUID.randomUUID().toString() + "-" + LocalDate.now().toString() + file.getOriginalFilename();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType(file.getContentType());
        objectMetadata.setContentLength(file.getSize());
        amazonS3.putObject(bucketFeature, fileName, file.getInputStream(), objectMetadata);
        return fileName;
    }

    /**
     * delete file trên cloud
     * @param oldFileName
     */
    private void deleteFromS3(String oldFileName) {
        amazonS3.deleteObject(bucketFeature, oldFileName);
    }

    /**
     * Lấy thông tin hình ảnh dạng presignedUrl
     * @param fileName
     * @return
     */
    private URL viewImageFromS3(String fileName){
        try {
            Date expiration = new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24);
            GeneratePresignedUrlRequest preSignedUrlRequest = new GeneratePresignedUrlRequest
                    (bucketFeature, fileName, HttpMethod.GET)
                    .withExpiration(expiration);
            return amazonS3.generatePresignedUrl(preSignedUrlRequest);
        }
        catch (Exception ex){
            throw new NotFoundException("Cannot find image with name " + fileName);
        }
    }

    /**
     * Validate file trước khi up lên cloud
     * @param file
     * @return
     */
    private boolean validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException("File is empty");
        }
        if (file.getSize() == 0){
            throw new AppException("File is empty");
        }
        String contentType = file.getContentType();
        if (!contentType.startsWith("image/")) {
            throw new AppException("File is not an image");
        }
        long maxSize = 5 * 1024 * 1024;
        if (file.getSize() > maxSize) {
            throw new AppException("File is too large");
        }
        return true;
    }
}
