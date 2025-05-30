package com.thacbao.codeSphere.entities.reference;

import com.thacbao.codeSphere.entities.core.Course;
import com.thacbao.codeSphere.entities.core.User;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "bills")
@Getter
@Setter
public class Bill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid_file", nullable = false, unique = true, length = 255)
    private String uuidFile;

    @Column(name = "payment_method", length = 255)
    private String paymentMethod;

    @Column(name = "total", nullable = false)
    private Long total;

    @Column(name = "product_details", columnDefinition = "JSON", nullable = false)
    private String productDetails;

    @Column(name = "created_by", length = 255)
    private String createdBy;

}
