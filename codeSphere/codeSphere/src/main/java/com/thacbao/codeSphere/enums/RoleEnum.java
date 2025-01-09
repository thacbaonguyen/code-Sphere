package com.thacbao.codeSphere.enums;

import lombok.Getter;

@Getter
public enum RoleEnum {
    ADMIN(1, "ADMIN", "ROLE_ADMIN"),
    USER(2, "USER", "ROLE_USER"),
    MANAGER(3, "MANAGER", "ROLE_MANAGER");

    private final Integer id;

    private final String name;

    private final String code;

    RoleEnum(Integer id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }
}
