package com.thacbao.codeSphere.dto.response.deserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContentItem {
    private int id;
    private String title;
    private String excerpt;
    private String featuredImage;
    private String author;
    private List<String> tagNames;
    private String status;
    private int viewCount;
    private String publishedAt;
    private int commentCount;
    private int totalReactions;
    private String slug;
    private String image;
    private boolean featured;
}
