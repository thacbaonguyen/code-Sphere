package com.thacbao.codeSphere.data.repository.course;

import com.thacbao.codeSphere.entities.reference.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer>, OrderRepositoryCustom {
    Order findByOrderCode(String orderCode);

    @Query(value = """
                    SELECT EXISTS (
                        SELECT 1 FROM orderdetails as od
                        JOIN orders as o ON od.order_id = o.id
                        WHERE od.course_id = :courseId and o.payment_status = 'paid' and o.user_id = :userId
                    )
                    """, nativeQuery = true)
    Integer existsByCourseId(@Param("courseId") Integer courseId, @Param("userId") Integer userId);

    default boolean checkExistsByCourseId(Integer courseId, Integer userId) {
        return existsByCourseId(courseId, userId) == 1;
    }
}
