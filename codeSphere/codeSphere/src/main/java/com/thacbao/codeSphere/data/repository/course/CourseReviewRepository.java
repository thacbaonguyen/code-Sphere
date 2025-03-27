package com.thacbao.codeSphere.data.repository.course;

import com.thacbao.codeSphere.entities.reference.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Integer> {

    @Query(value = "SELECT COALESCE(AVG(rating), 0) FROM coursereviews WHERE course_id = :courseId", nativeQuery = true)
    double averageRating(@Param("courseId") Integer courseId);
    @Query(value = "SELECT * FROM coursereviews WHERE course_id = :courseId", nativeQuery = true)
    List<CourseReview> findByCourseId(@Param("courseId") Integer courseId);
}
