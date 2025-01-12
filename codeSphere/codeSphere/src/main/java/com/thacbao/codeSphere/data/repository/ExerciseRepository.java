package com.thacbao.codeSphere.data.repository;

import com.thacbao.codeSphere.entity.core.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    Exercise findByCode(String code);
}
