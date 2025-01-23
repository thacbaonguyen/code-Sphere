package com.thacbao.codeSphere.dto.response.deserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pageable {
    private Sort sort;
    private int offset;
    private int pageSize;
    private int pageNumber;
    private boolean paged;
    private boolean unpaged;

}
