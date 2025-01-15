package com.thacbao.codeSphere.data.repository;

import com.thacbao.codeSphere.entity.core.Blog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface BlogRepository extends JpaRepository<Blog, Integer>, JpaSpecificationExecutor<Blog> {
    @Query(value = "SELECT * FROM blogs WHERE slug = :slug AND status = 'published'", nativeQuery = true)
    Optional<Blog> findBySlug(String slug);
}

