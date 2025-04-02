package com.thacbao.codeSphere.entities.reference;

import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.enums.PaymentStatus;
import lombok.Data;


import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "total_amount", nullable = false)
    private float totalAmount;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_status")
    private PaymentStatus paymentStatus = PaymentStatus.pending;

    @Column(name = "order_code")
    private String orderCode;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
