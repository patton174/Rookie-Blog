package com.lx.blog.common.constant;

/**
 * @author LX
 * @date 2025/12/10
 * @description 权限编码常量，用于注解等需要编译时常量的场景
 */
public interface PermissionConstants {

    // Article
    String ARTICLE_READ = "article:read";
    String ARTICLE_CREATE = "article:create";
    String ARTICLE_UPDATE = "article:update";
    String ARTICLE_DELETE = "article:delete";
    String ARTICLE_PUBLISH = "article:publish";
    String ARTICLE_AUDIT = "article:audit";
    String ARTICLE_TOP = "article:top";

    // Comment
    String COMMENT_READ = "comment:read";
    String COMMENT_CREATE = "comment:create";
    String COMMENT_DELETE = "comment:delete";
    String COMMENT_AUDIT = "comment:audit";

    // Category
    String CATEGORY_READ = "category:read";
    String CATEGORY_MANAGE = "category:manage";

    // Tag
    String TAG_READ = "tag:read";
    String TAG_MANAGE = "tag:manage";
    String TAG_CREATE = "tag:create";

    // File
    String FILE_UPLOAD = "file:upload";
    String FILE_DELETE = "file:delete";

    // User & RBAC
    String USER_READ = "user:read";
    String USER_UPDATE = "user:update";
    String USER_DELETE = "user:delete";
    String RBAC_MANAGE = "rbac:manage";
}
