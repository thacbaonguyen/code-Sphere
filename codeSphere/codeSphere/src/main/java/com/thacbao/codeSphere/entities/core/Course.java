package com.thacbao.codeSphere.entities.core;

import com.thacbao.codeSphere.entities.reference.*;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(name = "excerp", columnDefinition = "TEXT")
    private String excerpt;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnail;

    private float price;

    @Column(name = "is_active", length = 50)
    private boolean isActive;

    @Column(name = "created_at")
    private LocalDate createdAt;

    private int duration;
    private int discount;
    private float rate;

    @Column(name = "total_rate")
    private int totalRate;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private CourseCategory category;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<Section> sections;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<ShoppingCart> shoppingCarts;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<CourseReview> courseReviews;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL)
    private List<OrderDetail> orderDetail;
}
