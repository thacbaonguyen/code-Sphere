package com.thacbao.codeSphere.dto.request.blog;

import com.thacbao.codeSphere.entities.core.Blog;
import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.entities.reference.Tag;
import com.thacbao.codeSphere.enums.BlogStatus;
import com.thacbao.codeSphere.utils.SlugUtil;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BlogReq {
    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 255, message = "Title must be between 5 and 255 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @Size(max = 500, message = "Excerpt cannot exceed 500 characters")
    private String excerpt;

    private Set<String> tags = new HashSet<>();

    private String isFeatured;

    @Enumerated(EnumType.STRING)
    private BlogStatus status = BlogStatus.draft;

    public Blog toEntity(User author, Set<Tag> tags) {
        Blog blog = new Blog();
        blog.setTitle(this.title);
        blog.setContent(this.content);
        blog.setCreatedAt(LocalDate.now());
        blog.setUpdatedAt(LocalDate.now());
        blog.setExcerpt(this.excerpt);
        blog.setStatus(this.status);
        blog.setFeatured(Boolean.parseBoolean(this.isFeatured));
        blog.setAuthor(author);
        blog.setTags(tags);
        blog.setSlug(SlugUtil.generateSlug(this.title));
        blog.setPublishedAt(LocalDateTime.now());
        return blog;
    }
}
