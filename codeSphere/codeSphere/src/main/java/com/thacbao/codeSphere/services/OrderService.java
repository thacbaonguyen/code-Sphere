package com.thacbao.codeSphere.services;

import com.thacbao.codeSphere.entities.reference.Order;

public interface OrderService {
    Order createOrder();

    void saveOrder(Order order);

}
