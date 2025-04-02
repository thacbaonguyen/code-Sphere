package com.thacbao.codeSphere.data.repository.course;

import com.thacbao.codeSphere.entities.reference.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderDetailsRepository extends JpaRepository<OrderDetail, Integer> {
}
