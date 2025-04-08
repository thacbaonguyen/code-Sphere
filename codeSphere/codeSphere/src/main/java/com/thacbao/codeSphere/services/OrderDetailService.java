package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.entities.core.Course;
import com.thacbao.codeSphere.entities.reference.Order;
import com.thacbao.codeSphere.entities.reference.OrderDetail;

public interface OrderDetailService {
    void createOrderDetail(Order order);

    void createOrderDetailFree(Order order, Course course);
}
