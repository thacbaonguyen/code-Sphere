package com.thacbao.codeSphere.entities.core;

import com.thacbao.codeSphere.entities.reference.Bill;
import com.thacbao.codeSphere.entities.reference.ShoppingCart;
import com.thacbao.codeSphere.entities.reference.Video;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "courses")
@Getter
@Setter
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String thumbnail;

    private Double price;

    @Column(name = "is_active", length = 50)
    private String isActive;

    @Column(name = "created_at")
    private LocalDate createdAt;

    private int duration;
    private int discount;
    private float rate;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Video> videos;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ShoppingCart> shoppingCarts;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Bill> bills;
}
