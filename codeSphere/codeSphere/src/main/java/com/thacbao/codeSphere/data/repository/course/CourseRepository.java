package com.thacbao.codeSphere.data.repository.course;

import com.thacbao.codeSphere.entities.core.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer>, JpaSpecificationExecutor<Course> {
    Course findByTitle(String title);

    @Query("""
            SELECT c FROM Course c JOIN c.orderDetail od JOIN od.order o 
            WHERE c.id = :courseId AND o.user.id = :userId AND o.paymentStatus = 'paid'
            """)
    Course findByIdAndUserId(@Param("courseId")Integer id, @Param("userId") Integer userId);
}
