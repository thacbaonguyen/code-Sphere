package com.thacbao.codeSphere.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CmExHistoryDTO {
    private String content;

    private String updatedAt;
}
