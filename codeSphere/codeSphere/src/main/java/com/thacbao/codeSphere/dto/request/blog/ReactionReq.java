package com.thacbao.codeSphere.dto.request.blog;

import com.thacbao.codeSphere.enums.ReactionType;
import lombok.Data;

@Data
public class ReactionReq {
    private Integer blogId;
    private ReactionType reactionType;
}
