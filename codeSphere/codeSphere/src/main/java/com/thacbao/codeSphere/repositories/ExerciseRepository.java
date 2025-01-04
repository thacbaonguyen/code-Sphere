package com.thacbao.codeSphere.repositories;

import com.thacbao.codeSphere.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    Exercise findByCode(String code);
}
