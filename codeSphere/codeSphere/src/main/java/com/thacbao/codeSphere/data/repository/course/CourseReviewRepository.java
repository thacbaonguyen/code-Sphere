package com.thacbao.codeSphere.data.repository.course;

import com.thacbao.codeSphere.entities.reference.CourseReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.List;

@Repository
public interface CourseReviewRepository extends JpaRepository<CourseReview, Integer> {

    @Query(value = "SELECT COALESCE(AVG(rating), 0) FROM coursereviews WHERE course_id = :courseId", nativeQuery = true)
    double averageRating(@Param("courseId") Integer courseId);
    @Query(value = """
                    SELECT cv from CourseReview cv WHERE cv.course.id = :courseId order by cv.createdAt
                    """)
    List<CourseReview> findByCourseId(@Param("courseId") Integer courseId);

    @Query(value = "SELECT EXISTS (SELECT 1 FROM coursereviews WHERE course_id = :courseId AND user_id = :userId)", nativeQuery = true)
    BigInteger existsByUserId(@Param("courseId") Integer courseId, @Param("userId") Integer userId);

    default boolean exists(Integer courseId, Integer userId) {
        BigInteger result = existsByUserId(courseId, userId);
        return result != null && result.intValue() == 1;
    }
}
