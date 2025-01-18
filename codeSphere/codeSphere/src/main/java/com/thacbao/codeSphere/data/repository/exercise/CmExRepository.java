package com.thacbao.codeSphere.data.repository.exercise;

import com.thacbao.codeSphere.entities.reference.CommentExercise;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CmExRepository extends JpaRepository<CommentExercise, Integer> {
}
