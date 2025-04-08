package com.thacbao.codeSphere.services.blogImpl;

import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.data.repository.blog.BlogRepository;
import com.thacbao.codeSphere.data.repository.blog.CommentBlogRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.dto.request.blog.CommentBlogReq;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.blog.CmtBlogDTO;
import com.thacbao.codeSphere.entities.core.Blog;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.CommentBlog;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.services.CommentBlogService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentBlogServiceImpl implements CommentBlogService {
    private final CommentBlogRepository commentBlogRepository;

    private final UserRepository userRepository;

    private final JwtFilter jwtFilter;

    private final BlogRepository blogRepository;

    private final CommentBlogHistoryService commentBlogHistoryService;
    private final CustomUserDetailsService userDetailsService;
    @Override
    @Transactional
    public ResponseEntity<ApiResponse> insertComment(CommentBlogReq request) {
        try {
            CommentBlog commentBlog = mapCommentBlog(request);
            commentBlogRepository.save(commentBlog);
            return CodeSphereResponses.generateResponse(null, "Insert comment blog successfully", HttpStatus.CREATED);
        }
        catch (NotFoundException ex) {
            log.error("logging error with message {}", ex.getMessage(), ex.getCause());
            return CodeSphereResponses.generateResponse(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> viewAllCommentWithBlog(Integer blogId) {
        List<CommentBlog> commentBlogs = commentBlogRepository.findByBlogId(blogId);
        List<CmtBlogDTO> result = commentBlogs.stream().map(
                item -> new CmtBlogDTO(item)
        ).toList();
        return CodeSphereResponses.generateResponse(result, "View all comments successfully", HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> updateComment(Integer id, CommentBlogReq request) {
        CommentBlog commentBlog = getCommentBlog(id);
        commentBlog.setContent(request.getContent());
        commentBlog.setUpdatedAt(LocalDateTime.now());
        commentBlogRepository.save(commentBlog);
        commentBlogHistoryService.insertCommentBlogHistory(request.getContent(), commentBlog);
        return CodeSphereResponses.generateResponse(null, "Update comment blog successfully", HttpStatus.OK);
    }

    @Override
    @Transactional
    public ResponseEntity<ApiResponse> deleteComment(Integer id) {
        CommentBlog commentBlog = getCommentBlog(id);
        commentBlogRepository.delete(commentBlog);
        return CodeSphereResponses.generateResponse(null, "Delete comment successfully", HttpStatus.OK);
    }

    private CommentBlog mapCommentBlog(CommentBlogReq request){
        return CommentBlog.builder()
                .content(request.getContent())
                .user(getUser())
                .blog(getBlog(request.getBlogId()))
                .parentComment(request.getParentId() != null ? getCommentBlog(request.getParentId()) : null)
                .authorName(jwtFilter.getCurrentUsername())
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    private User getUser() {
        return userDetailsService.getUserDetails();
    }

    private Blog getBlog(Integer id) {
        return blogRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Blog not found")
        );
    }

    private CommentBlog getCommentBlog(Integer id) {
        return commentBlogRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Comment blog not found")
        );
    }
}
