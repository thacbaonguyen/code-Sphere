package com.thacbao.codeSphere.data.repository.exercise;

import com.thacbao.codeSphere.entities.reference.SubmissionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmissionRepository extends JpaRepository<SubmissionHistory, Integer> {
    @Query(value = "select * from submissionhistories as s" +
            " join exercises as e on s.exercise_id = e.id " +
            " join users as u on  s.user_id = u.id " +
            " where e.code = :code and u.user_name = :username", nativeQuery = true)
    List<SubmissionHistory> findByExerciseCodeAndUsername(@Param("code") String code, @Param("username") String username);
}
