package com.solosw.codelab.enums;

public enum HouseRightEnum {

    READ_ONLY("R"),       // 只读权限
    READ_WRITE("RW"),     // 读写权限
    READ_WRITE_FORCE("RW+"), // 读写及强制更新权限
    NONE("-"),            // 没有权限
    CREATE("C"),          // 创建权限
    DELETE("D");          // 删除权限

    private final String permission;

    HouseRightEnum(String permission) {
        this.permission = permission;
    }

    public String getPermission() {
        return permission;
    }

    @Override
    public String toString() {
        return String.format("%s: %s", name(), permission);
    }
}
