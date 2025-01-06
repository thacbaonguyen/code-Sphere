package com.thacbao.codeSphere.repositories;

import com.thacbao.codeSphere.entity.CommentExercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CmExRepository extends JpaRepository<CommentExercise, Integer> {
}
