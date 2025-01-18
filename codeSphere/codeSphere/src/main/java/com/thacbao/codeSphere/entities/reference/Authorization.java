package com.thacbao.codeSphere.entities.reference;

import com.thacbao.codeSphere.entities.core.User;
import com.thacbao.codeSphere.id.AuthorizationId;
import lombok.Getter;
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
