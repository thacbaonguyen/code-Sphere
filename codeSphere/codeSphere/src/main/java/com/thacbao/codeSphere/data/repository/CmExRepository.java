package com.thacbao.codeSphere.data.repository;

import com.thacbao.codeSphere.entity.reference.CommentExercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CmExRepository extends JpaRepository<CommentExercise, Integer> {
}
