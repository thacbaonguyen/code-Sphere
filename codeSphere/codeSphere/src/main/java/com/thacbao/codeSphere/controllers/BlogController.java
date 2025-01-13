package com.thacbao.codeSphere.controllers;

import com.thacbao.codeSphere.dto.request.blog.BlogReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.services.BlogService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/view/{slug}")
    public ResponseEntity<ApiResponse> view(@PathVariable String slug){

        return blogService.viewBlog(slug);
    }

    @GetMapping("/all-blogs")
    public ResponseEntity<ApiResponse> findAllBlogs(@RequestParam(required = false) String search,
                                                @RequestParam(required = false) String isFeatured,
                                                @RequestParam(defaultValue = "0") Integer page,
                                                @RequestParam(defaultValue = "20") Integer pageSize,
                                                @RequestParam(required = false) String order,
                                                @RequestParam(required = false) String by){

        return blogService.getAllBlogs(search, isFeatured, page, pageSize, order, by);
    }

    @GetMapping("/list/tags")
    public ResponseEntity<ApiResponse> findAllByTags(@RequestParam(defaultValue = "") String tagName,
                                                     @RequestParam(required = false) String isFeatured,
                                                     @RequestParam(defaultValue = "0") Integer page,
                                                     @RequestParam(defaultValue = "20") Integer pageSize,
                                                     @RequestParam(required = false) String order,
                                                     @RequestParam(required = false) String by){
        return blogService.findAllByTags(tagName, isFeatured, page, pageSize, order, by);
    }

}
