package com.lx.blog.common.enums;

import com.lx.blog.common.constant.PermissionConstants;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LX
 * @date 2025/12/10
 * @description 权限枚举
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PermissionEnum {

    // Article
    ARTICLE_READ(PermissionConstants.ARTICLE_READ, "阅读文章", "允许查看文章详情"),
    ARTICLE_CREATE(PermissionConstants.ARTICLE_CREATE, "创建文章", "允许创建新文章"),
    ARTICLE_UPDATE(PermissionConstants.ARTICLE_UPDATE, "修改文章", "允许修改文章"),
    ARTICLE_DELETE(PermissionConstants.ARTICLE_DELETE, "删除文章", "允许删除文章"),
    ARTICLE_PUBLISH(PermissionConstants.ARTICLE_PUBLISH, "发布文章", "允许发布文章"),
    ARTICLE_AUDIT(PermissionConstants.ARTICLE_AUDIT, "审核文章", "允许审核文章"),
    ARTICLE_TOP(PermissionConstants.ARTICLE_TOP, "置顶文章", "允许置顶文章"),

    // Comment
    COMMENT_READ(PermissionConstants.COMMENT_READ, "阅读评论", "允许查看评论列表"),
    COMMENT_CREATE(PermissionConstants.COMMENT_CREATE, "发表评论", "允许发表评论"),
    COMMENT_DELETE(PermissionConstants.COMMENT_DELETE, "删除评论", "允许删除任意评论"),
    COMMENT_AUDIT(PermissionConstants.COMMENT_AUDIT, "审核评论", "允许审核/隐藏评论"),

    // Category
    CATEGORY_READ(PermissionConstants.CATEGORY_READ, "阅读分类", "允许查看分类"),
    CATEGORY_MANAGE(PermissionConstants.CATEGORY_MANAGE, "管理分类", "允许增删改分类"),

    // Tag
    TAG_READ(PermissionConstants.TAG_READ, "阅读标签", "允许查看标签"),
    TAG_MANAGE(PermissionConstants.TAG_MANAGE, "管理标签", "允许增删改标签"),
    TAG_CREATE(PermissionConstants.TAG_CREATE, "创建标签", "允许创建新标签"),

    // File
    FILE_UPLOAD(PermissionConstants.FILE_UPLOAD, "文件上传", "允许上传文件"),
    FILE_DELETE(PermissionConstants.FILE_DELETE, "文件删除", "允许删除文件"),

    // User & RBAC
    USER_READ(PermissionConstants.USER_READ, "查看用户", "允许查看用户列表和详情"),
    USER_UPDATE(PermissionConstants.USER_UPDATE, "管理用户", "允许修改用户信息"),
    USER_DELETE(PermissionConstants.USER_DELETE, "删除用户", "允许封禁或删除用户"),
    RBAC_MANAGE(PermissionConstants.RBAC_MANAGE, "权限管理", "允许管理角色和权限");

    /**
     * 权限编码
     */
    private final String code;
    /**
     * 权限名称
     */
    private final String name;
    /**
     * 权限描述
     */
    private final String description;
}
