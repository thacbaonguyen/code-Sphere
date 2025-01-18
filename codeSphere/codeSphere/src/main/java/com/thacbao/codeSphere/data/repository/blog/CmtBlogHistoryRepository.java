package com.thacbao.codeSphere.data.repository.blog;

import com.thacbao.codeSphere.entities.reference.CmtBlogHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CmtBlogHistoryRepository extends JpaRepository<CmtBlogHistory, Integer> {
    @Query(value = "select * from cmtbloghistory where comment_id = :commentBlogId", nativeQuery = true)
    List<CmtBlogHistory> findByCommentBlogId(@Param("commentBlogId") Integer commentBlogId);
}
