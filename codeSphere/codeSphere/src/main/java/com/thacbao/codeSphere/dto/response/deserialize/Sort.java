package com.thacbao.codeSphere.dto.response.deserialize;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Sort {
    private boolean empty;
    private boolean sorted;
    private boolean unsorted;
}
