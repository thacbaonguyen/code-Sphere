package com.thacbao.codeSphere.entities.reference;

import com.mysql.cj.protocol.ColumnDefinition;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cmtbloghistory")
@Data
public class CmtBlogHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "comment_id", nullable = false)
    private CommentBlog commentBlog;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
