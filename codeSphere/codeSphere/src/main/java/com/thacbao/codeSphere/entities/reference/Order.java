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

    private PaymentStatus paymentStatus = PaymentStatus.pending;

    @Column(name = "transaction_id")
    private String transactionId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
