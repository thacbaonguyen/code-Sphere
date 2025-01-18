package com.thacbao.codeSphere.data.repository.blog;

import com.thacbao.codeSphere.entities.reference.CommentBlog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentBlogRepository extends JpaRepository<CommentBlog, Integer> {
    List<CommentBlog> findByBlogId(Integer blogId);
}
