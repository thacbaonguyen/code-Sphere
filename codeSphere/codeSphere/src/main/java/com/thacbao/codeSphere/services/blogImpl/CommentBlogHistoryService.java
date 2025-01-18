package com.thacbao.codeSphere.services.blogImpl;

import com.thacbao.codeSphere.data.repository.blog.CmtBlogHistoryRepository;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.dto.response.blog.CmtBlogHistoryDTO;
import com.thacbao.codeSphere.entities.reference.CmtBlogHistory;
import com.thacbao.codeSphere.entities.reference.CommentBlog;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Log4j2
@Transactional
public class CommentBlogHistoryService {

    private final CmtBlogHistoryRepository cmtBlogHistoryRepository;
    @Transactional
    public void insertCommentBlogHistory(String content, CommentBlog commentBlog) {
        CmtBlogHistory cmtBlogHistory = new CmtBlogHistory();
        cmtBlogHistory.setCommentBlog(commentBlog);
        cmtBlogHistory.setContent(content);
        cmtBlogHistory.setUpdatedAt(LocalDateTime.now());
        cmtBlogHistoryRepository.save(cmtBlogHistory);
        log.info("Insert comment blog history");
    }

    @Transactional
    public ResponseEntity<ApiResponse> getAllCommentBlogHistory(Integer commentBlogId) {
        List<CmtBlogHistory> cmtBlogHistories = cmtBlogHistoryRepository
                .findByCommentBlogId(commentBlogId);
        List<CmtBlogHistoryDTO> result = cmtBlogHistories.stream().map(
                item -> new CmtBlogHistoryDTO(item)
        ).toList();
        return CodeSphereResponses.generateResponse(result, "All cmt history blog ", HttpStatus.OK);
    }
}
