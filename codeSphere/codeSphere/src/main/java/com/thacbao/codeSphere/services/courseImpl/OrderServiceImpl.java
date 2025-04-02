package com.thacbao.codeSphere.services.courseImpl;

import com.thacbao.codeSphere.configurations.JwtFilter;
import com.thacbao.codeSphere.constants.CodeSphereConstants;
import com.thacbao.codeSphere.data.repository.course.CartRepository;
import com.thacbao.codeSphere.data.repository.course.OrderRepository;
import com.thacbao.codeSphere.data.repository.user.UserRepository;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.Order;
import com.thacbao.codeSphere.entities.reference.ShoppingCart;
import com.thacbao.codeSphere.enums.PaymentStatus;
import com.thacbao.codeSphere.exceptions.common.AppException;
import com.thacbao.codeSphere.exceptions.common.NotFoundException;
import com.thacbao.codeSphere.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final UserRepository userRepository;
    @Override
    public Order createOrder() {
        User user = userRepository.findByUsername(jwtFilter.getCurrentUsername()).orElseThrow(
                ()-> new NotFoundException(CodeSphereConstants.User.USER_NOT_FOUND)
        );
        List<ShoppingCart> carts = cartRepository.findByUser(jwtFilter.getCurrentUsername());
        if (carts.isEmpty()) {
            throw new AppException("Cart is empty");
        }

        long totalAmount = 0L;
        for (ShoppingCart cart : carts) {
            long price = (long) (cart.getCourse().getPrice() + (cart.getCourse().getDiscount() * cart.getCourse().getPrice())/100);
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
}
