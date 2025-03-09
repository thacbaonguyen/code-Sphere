package com.thacbao.codeSphere.data.repository.exercise;

import com.thacbao.codeSphere.entities.reference.TestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TestCaseRepository extends JpaRepository<TestCase, Integer>{

    List<TestCase> findByExerciseId(Integer exerciseId);

    @Query(value = "SELECT t.id, t.input, t.output, t.exercise_id FROM testcases as t" +
            " JOIN exercises on t.exercise_id = exercises.id WHERE exercises.code =:code " +
            " group by t.id", nativeQuery = true)
    List<TestCase> findByExerciseCode(String code);
}
