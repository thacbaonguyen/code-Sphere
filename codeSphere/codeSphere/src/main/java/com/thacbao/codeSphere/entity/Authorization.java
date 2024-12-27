package com.thacbao.codeSphere.entity;

import com.thacbao.codeSphere.id.AuthorizationId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
@Entity
@Table(name = "authorization")
@Getter
@Setter
public class Authorization{
    @EmbeddedId
    private AuthorizationId id;

    @ManyToOne
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @MapsId("roleId")
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    public Authorization(User user, Role role) {
        this.user = user;
        this.role = role;
    }

    public Authorization() {

    }
}
