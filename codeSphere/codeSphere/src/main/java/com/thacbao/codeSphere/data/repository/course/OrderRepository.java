package com.thacbao.codeSphere.data.repository.course;

import com.thacbao.codeSphere.entities.reference.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
}
