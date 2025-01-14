package com.thacbao.codeSphere.entity.reference;

import com.thacbao.codeSphere.entity.core.User;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "registerroles")
@Data
public class RegisterRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "is_accepted")
    private boolean isAccepted;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    private Role role;
}
