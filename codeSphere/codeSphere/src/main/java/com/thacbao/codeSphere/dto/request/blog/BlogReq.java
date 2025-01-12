package com.thacbao.codeSphere.dto.request.blog;

import com.thacbao.codeSphere.enums.BlogStatus;
import lombok.*;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.*;
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

    private String featuredImage;

    private Set<Long> tagIds = new HashSet<>();

    private boolean isFeatured;

    @Enumerated(EnumType.STRING)
    private BlogStatus status = BlogStatus.DRAFT;

}
