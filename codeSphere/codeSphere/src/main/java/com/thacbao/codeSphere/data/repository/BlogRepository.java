package com.thacbao.codeSphere.data.repository;

import com.thacbao.codeSphere.entity.core.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer> {
    Optional<Blog> findBySlug(String slug);
}
