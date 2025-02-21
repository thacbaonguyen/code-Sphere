package com.thacbao.codeSphere.data.repository.exercise;

import com.thacbao.codeSphere.entities.core.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {
    Exercise findByCode(String code);

    @Query(value = "SELECT COUNT(*) FROM exercises WHERE is_active = true", nativeQuery = true)
    long count();
}
