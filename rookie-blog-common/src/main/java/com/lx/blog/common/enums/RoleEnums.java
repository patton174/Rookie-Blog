package com.lx.blog.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LX
 * @date 2025/12/10
 * @description 角色枚举
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RoleEnums {

    ADMIN("admin", "管理员", "系统管理员，拥有全站最高权限"),
    AUTHOR("author", "作者", "内容创作者，可发布与管理文章"),
    READER("reader", "读者", "注册用户，可评论与互动"),
    GUEST("guest", "游客", "未登录访客，仅供浏览");

    /**
     * 角色编码
     */
    private final String code;
    /**
     * 角色名称
     */
    private final String name;
    /**
     * 角色描述
     */
    private final String description;
}
