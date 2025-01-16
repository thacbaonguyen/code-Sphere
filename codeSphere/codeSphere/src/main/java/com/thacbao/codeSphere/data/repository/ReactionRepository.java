package com.thacbao.codeSphere.data.repository;

import com.thacbao.codeSphere.entity.core.Blog;
import com.thacbao.codeSphere.entity.core.User;
import com.thacbao.codeSphere.entity.reference.Reaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ReactionRepository extends JpaRepository<Reaction, Integer> {
    Optional<Reaction> findByUserAndBlog(User user, Blog blog);

    @Modifying
    @Query(value = "DELETE FROM reactions WHERE user_id = :userId AND blog_id = :blogId", nativeQuery = true)
    void deleteCustom(@Param("userId") Integer userId, @Param("blogId") Integer blogId);
}
