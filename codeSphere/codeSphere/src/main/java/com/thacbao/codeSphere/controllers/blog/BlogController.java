package com.thacbao.codeSphere.controllers.blog;

import com.thacbao.codeSphere.dto.request.blog.BlogReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.BlogService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/blog")
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    @PostMapping("/insert")
    public ResponseEntity<ApiResponse> insert(@Valid @RequestBody BlogReq request, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }

        return blogService.insertBlog(request);
    }

    @PostMapping("/upload/feature-image/{blog-id}")
    public void uploadFeatureImage(@PathVariable("blog-id") Integer blogId,
                                                          @RequestParam("featureImage") MultipartFile file) {
        blogService.uploadFeatureImage(blogId, file);
    }

    @GetMapping("/view/{slug}")
    public ResponseEntity<ApiResponse> view(@PathVariable String slug){

        return blogService.viewBlog(slug);
    }

    @GetMapping("/all-blogs")
    public ResponseEntity<ApiResponse> findAllBlogs(@RequestParam(required = false) String search,
                                                @RequestParam(required = false) String isFeatured,
                                                @RequestParam(defaultValue = "1") Integer page,
                                                @RequestParam(defaultValue = "15") Integer pageSize,
                                                @RequestParam(required = false) String order,
                                                @RequestParam(required = false) String by,
                                                    @RequestParam(defaultValue = "published") String status){

        return blogService.getAllBlogs(search, isFeatured, page, pageSize, order, by, status);
    }

    @GetMapping("/all-blogs/tags")
    public ResponseEntity<ApiResponse> findAllByTags(@RequestParam(defaultValue = "") String tagName,
                                                     @RequestParam(required = false) String isFeatured,
                                                     @RequestParam(defaultValue = "1") Integer page,
                                                     @RequestParam(defaultValue = "15") Integer pageSize,
                                                     @RequestParam(required = false) String order,
                                                     @RequestParam(required = false) String by,
                                                     @RequestParam(defaultValue = "published") String status){
        return blogService.findAllByTags(tagName, isFeatured, page, pageSize, order, by, status);
    }

    @GetMapping("/my-blog")
    public ResponseEntity<ApiResponse> findMyBlogs(@RequestParam(required = false) String search,
                                                   @RequestParam(required = false) String status,
                                                   @RequestParam(required = false) String order,
                                                   @RequestParam(required = false) String by){

        return blogService.findMyBlogs(search, status, order, by);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ApiResponse> updateBlog(@PathVariable Integer id,
                                                  @Valid @RequestBody BlogReq request, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(fieldError -> {
                errors.put(fieldError.getField(), fieldError.getDefaultMessage());
            });
            return CodeSphereResponses.generateResponse(errors, "Validation failed", HttpStatus.BAD_REQUEST);
        }
        return blogService.updateBlog(id, request);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Integer id){
        return blogService.deleteBlog(id);
    }
}
