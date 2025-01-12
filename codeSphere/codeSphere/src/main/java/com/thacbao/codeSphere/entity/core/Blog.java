package com.thacbao.codeSphere.entity.core;

import com.thacbao.codeSphere.entity.reference.CommentBlog;
import com.thacbao.codeSphere.entity.reference.Reaction;
import com.thacbao.codeSphere.entity.reference.Tag;
import com.thacbao.codeSphere.enums.BlogStatus;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "blogs")
@Getter
@Setter
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name = "created_at")
    private LocalDate createdAt;

    @Column(name = "updated_at")
    private LocalDate updatedAt;

    @Column(columnDefinition = "TEXT")
    private String excerpt;

    @Column(name = "featured_image")
    private String featuredImage;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private BlogStatus status = BlogStatus.DRAFT;

    @Column(name = "is_featured")
    private boolean isFeatured = false;

    @Column(name = "view_count")
    private int viewCount = 0;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @ManyToOne
    @JoinColumn(name = "author_id", referencedColumnName = "id")
    private User author;

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CommentBlog> comments = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "ontags",
            joinColumns = @JoinColumn(name = "blog_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reaction> reactions = new ArrayList<>();

}
