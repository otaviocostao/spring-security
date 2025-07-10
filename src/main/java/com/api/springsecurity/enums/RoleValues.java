package com.api.springsecurity.enums;

public enum RoleValues {
    ADMIN(1L),
    BASIC(2L);

    long roleId;

    RoleValues(long roleId) {
        this.roleId = roleId;
    }

    public long getRoleId() {
        return roleId;
    }
}
