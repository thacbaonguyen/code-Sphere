package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.entities.reference.Order;
import com.thacbao.codeSphere.entities.reference.OrderDetail;

public interface OrderDetailService {
    void createOrderDetail(Order order);
}
