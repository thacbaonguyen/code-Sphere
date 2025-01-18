package com.thacbao.codeSphere.data.repository.exercise;

import com.thacbao.codeSphere.entities.reference.SolutionStorage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SolutionRepository extends JpaRepository<SolutionStorage, Integer> {

    @Query(value = "SELECT COUNT(1) FROM solutionstorage WHERE exercise_id = :exerciseId AND user_id = :userId", nativeQuery = true)
    Integer countSolution(@Param("exerciseId") Integer exerciseId, @Param("userId") Integer userId);

    Optional<SolutionStorage> findByFilename(String fileName);

    List<SolutionStorage> findByExerciseIdAndUserId(Integer exerciseId, Integer userId);
}
