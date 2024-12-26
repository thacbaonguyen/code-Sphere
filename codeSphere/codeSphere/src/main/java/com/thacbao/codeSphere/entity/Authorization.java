package com.thacbao.codeSphere.entity;

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

}
