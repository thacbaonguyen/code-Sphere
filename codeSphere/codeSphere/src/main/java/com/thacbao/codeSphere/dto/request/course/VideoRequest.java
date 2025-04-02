package com.thacbao.codeSphere.dto.request.course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoRequest {
    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotNull(message = "Order index cannot be null")
    private int orderIndex;
    @NotNull(message = "Section id cannot be null")
    private int sectionId;
}
