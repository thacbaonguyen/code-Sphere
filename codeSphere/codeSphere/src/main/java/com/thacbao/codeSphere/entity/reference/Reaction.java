package com.thacbao.codeSphere.entity.reference;

import com.thacbao.codeSphere.entity.core.Blog;
import com.thacbao.codeSphere.entity.core.User;
import com.thacbao.codeSphere.enums.ReactionType;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "reactions")
@Getter
@Setter
public class Reaction{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", nullable = false)
    private Blog blog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(name = "reaction_type", length = 20)
    private ReactionType reactionType = ReactionType.LIKE;
}
