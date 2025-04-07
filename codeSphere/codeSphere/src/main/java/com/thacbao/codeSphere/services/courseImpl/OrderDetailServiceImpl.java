package com.thacbao.codeSphere.services.courseImpl;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.data.repository.course.CartRepository;
import com.thacbao.codeSphere.data.repository.course.OrderDetailsRepository;
import com.thacbao.codeSphere.entities.reference.Order;
import com.thacbao.codeSphere.entities.reference.OrderDetail;
import com.thacbao.codeSphere.entities.reference.ShoppingCart;
import com.thacbao.codeSphere.services.OrderDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderDetailsRepository orderDetailsRepository;
    private final CartRepository cartRepository;
    private final JwtFilter jwtFilter;
    @Override
    public void createOrderDetail(Order order) {
        List<ShoppingCart> carts = cartRepository.findByUser(jwtFilter.getCurrentUsername());
        List<OrderDetail> orderDetails = carts.stream()
                .map(cartItem -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrder(order);
                    orderDetail.setCourse(cartItem.getCourse());
                    orderDetail.setPrice(cartItem.getCourse().getPrice());
                    return orderDetail;
                })
                .collect(Collectors.toList());
        orderDetailsRepository.saveAll(orderDetails);
//        cartRepository.deleteByUser(jwtFilter.getCurrentUsername());
    }
}
