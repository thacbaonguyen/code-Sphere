package com.thacbao.codeSphere.services.courseImpl;

import com.thacbao.codeSphere.configurations.CustomUserDetailsService;
import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.data.repository.course.CartRepository;
import com.thacbao.codeSphere.data.repository.course.OrderRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.dto.request.course.OrderConfirmRequest;
import com.thacbao.codeSphere.dto.response.ApiResponse;
import com.thacbao.codeSphere.entities.core.Course;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.Order;
import com.thacbao.codeSphere.entities.reference.ShoppingCart;
import com.thacbao.codeSphere.enums.PaymentStatus;
import com.thacbao.codeSphere.exceptions.common.AppException;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.services.OrderDetailService;
import com.thacbao.codeSphere.services.OrderService;
import com.thacbao.codeSphere.services.redis.RedisService;
import com.thacbao.codeSphere.utils.CodeSphereResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final JwtFilter jwtFilter;
    private final OrderDetailService orderDetailService;
    private final CustomUserDetailsService customUserDetailsService;
    private final CartRepository shoppingCartRepository;
    private final RedisService redisService;
    @Override
    public Order createOrder() {
        User user = customUserDetailsService.getUserDetails();
        List<ShoppingCart> carts = cartRepository.findByUser(jwtFilter.getCurrentUsername());
        if (carts.isEmpty()) {
            throw new AppException("Cart is empty");
        }

        long totalAmount = 0L;
        for (ShoppingCart cart : carts) {
            long price = (long) (cart.getCourse().getPrice() - (cart.getCourse().getDiscount() * cart.getCourse().getPrice())/100);
            totalAmount += price;
        }
        Order order = new Order();
        order.setTotalAmount(totalAmount);
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.pending);
        order.setUser(user);
        return orderRepository.save(order);
    }

    @Override
    public void saveOrder(Order order) {
        orderRepository.save(order);
    }

    @Override
    public Order createFreeOrder(Course course) {
        Order order = new Order();
        order.setTotalAmount(course.getPrice());
        order.setOrderDate(LocalDateTime.now());
        order.setPaymentStatus(PaymentStatus.paid);
        order.setUser(customUserDetailsService.getUserDetails());
        try {
            Order orderSave = orderRepository.save(order);
            orderDetailService.createOrderDetailFree(orderSave, course);
            redisService.delete("myCourses:" + jwtFilter.getCurrentUsername());
            return orderSave;
        }
        catch (AppException e) {
            throw new AppException("Error create order free");
        }

    }

    @Override
    public ResponseEntity<ApiResponse> updateStatus(OrderConfirmRequest request) {
        Order order = orderRepository.findByOrderCode(request.getOrderCode());
        if (order == null) {
            return CodeSphereResponses.generateResponse(null, "Cannot found this order", HttpStatus.NOT_FOUND);
        }
        if (request.getStatus().equalsIgnoreCase("pending")) {
            order.setPaymentStatus(PaymentStatus.pending);
            orderRepository.save(order);
        } else if (request.getStatus().equalsIgnoreCase("paid")) {
            order.setPaymentStatus(PaymentStatus.paid);
            orderRepository.save(order);
            redisService.delete("Cart:" + order.getUser().getUsername());
            redisService.delete("myCourses:" + order.getUser().getUsername());
        } else if (request.getStatus().equalsIgnoreCase("cancelled")) {
            order.setPaymentStatus(PaymentStatus.cancelled);
            orderRepository.save(order);
        }
        shoppingCartRepository.deleteByUser(order.getUser().getUsername());
        return CodeSphereResponses.generateResponse(null, "Success", HttpStatus.OK);
    }
}
