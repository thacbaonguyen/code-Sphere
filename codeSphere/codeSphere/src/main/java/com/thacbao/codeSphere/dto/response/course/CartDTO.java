package com.thacbao.codeSphere.dto.response.course;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.thacbao.codeSphere.entities.reference.ShoppingCart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartDTO {
    private Integer id;
    @JsonProperty("courseBriefDTO")
    private CourseBriefDTO courseBriefDTO;

    public CartDTO(ShoppingCart shoppingCart) {

        this.id = shoppingCart.getId();
        this.courseBriefDTO = new CourseBriefDTO(shoppingCart.getCourse());
    }
}
