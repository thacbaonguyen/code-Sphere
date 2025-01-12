package com.thacbao.codeSphere.entity.core;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "ebooks")
@Getter
@Setter
public class Ebook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "uuid_file", nullable = false, unique = true, length = 255)
    private String uuidFile;

    @Column(name = "created_by", length = 255)
    private String createdBy;

    @Column(name = "image", length = 255)
    private String image;
}
