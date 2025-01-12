package com.thacbao.codeSphere.dto.response.blog;

import com.thacbao.codeSphere.entity.core.Blog;
import com.thacbao.codeSphere.enums.BlogStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogBriefDTO {
    private Integer id;
    private String title;
    private String excerpt;
    private String featuredImage;
    private String author;
    private Set<String> tagNames;
    private BlogStatus status;
    private boolean isFeatured;
    private int viewCount;
    private LocalDateTime publishedAt;
    private long commentCount;
    private long totalReactions;
    private String slug;

    public BlogBriefDTO(Blog blog) {
        this.id = blog.getId();
        this.title = blog.getTitle();
        this.excerpt = blog.getExcerpt();
        this.featuredImage = blog.getFeaturedImage();
        this.author = blog.getAuthor().getUsername();
        this.tagNames = blog.getTags().stream().map(rs -> rs.getName()).collect(Collectors.toSet());
        this.status = blog.getStatus();
        this.isFeatured = blog.isFeatured();
        this.viewCount = blog.getViewCount();
        this.publishedAt = blog.getPublishedAt();
        this.commentCount = blog.getComments().size();
        this.totalReactions = blog.getReactions().size();
        this.slug = blog.getSlug();
    }
}
