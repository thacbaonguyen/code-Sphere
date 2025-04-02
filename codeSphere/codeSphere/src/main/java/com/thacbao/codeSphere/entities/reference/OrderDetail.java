package com.thacbao.codeSphere.entities.reference;

import com.thacbao.codeSphere.entities.core.Course;
import lombok.Data;


import javax.persistence.*;

@Entity
@Table(name = "orderdetails")
@Data
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "course_price")
    private float price;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "course_id")
    private Course course;
}
