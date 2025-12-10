package com.lx.blog.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LX
 * @date 2025/12/10
 * @description 用户状态枚举
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum UserStatusEnums {

    NORMAL("1", "正常", "用户账号正常"),
    DISABLED("0", "禁用", "用户账号已被禁用，无法登录");

    /**
     * 用户状态编码
     */
    private final String code;
    /**
     * 用户状态名称
     */
    private final String name;
    /**
     * 用户状态描述
     */
    private final String description;
}
