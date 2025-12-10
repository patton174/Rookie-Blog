-- 初始化RBAC（角色-权限）数据脚本
-- 警告：此脚本会清空现有的 role, permission, role_permission 表数据！

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- 1. 清理旧数据
TRUNCATE TABLE `role_permission`;
DELETE FROM `permission`;
DELETE FROM `role`;

-- 2. 定义角色 (使用变量暂存ID)
SET @role_admin   = REPLACE(UUID(), '-', '');
SET @role_author  = REPLACE(UUID(), '-', '');
SET @role_reader  = REPLACE(UUID(), '-', '');
SET @role_guest   = REPLACE(UUID(), '-', '');

INSERT INTO `role` (`id`, `code`, `name`, `description`) VALUES 
(@role_admin,  'admin',  '管理员', '系统管理员，拥有全站最高权限'),
(@role_author, 'author', '作者',   '内容创作者，可发布与管理文章'),
(@role_reader, 'reader', '读者',   '注册用户，可评论与互动'),
(@role_guest,  'guest',  '游客',   '未登录访客，仅供浏览');

-- 3. 定义权限并分配
-- 辅助函数或存储过程在脚本中较繁琐，这里直接按模块批量插入并分配

-- =========================================================
-- 模块：文章 (Article)
-- =========================================================
SET @p_article_read    = REPLACE(UUID(), '-', '');
SET @p_article_create  = REPLACE(UUID(), '-', '');
SET @p_article_update  = REPLACE(UUID(), '-', '');
SET @p_article_delete  = REPLACE(UUID(), '-', '');
SET @p_article_publish = REPLACE(UUID(), '-', '');
SET @p_article_audit   = REPLACE(UUID(), '-', '');
SET @p_article_top     = REPLACE(UUID(), '-', '');

INSERT INTO `permission` (`id`, `code`, `name`, `description`) VALUES
(@p_article_read,    'article:read',    '阅读文章', '允许查看文章详情'),
(@p_article_create,  'article:create',  '创建文章', '允许创建新文章'),
(@p_article_update,  'article:update',  '修改文章', '允许修改文章'),
(@p_article_delete,  'article:delete',  '删除文章', '允许删除文章'),
(@p_article_publish, 'article:publish', '发布文章', '允许发布文章'),
(@p_article_audit,   'article:audit',   '审核文章', '允许审核文章'),
(@p_article_top,     'article:top',     '置顶文章', '允许置顶文章');

-- 分配 article:read 给所有角色
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(REPLACE(UUID(), '-', ''), @role_guest,  @p_article_read),
(REPLACE(UUID(), '-', ''), @role_reader, @p_article_read),
(REPLACE(UUID(), '-', ''), @role_author, @p_article_read),
(REPLACE(UUID(), '-', ''), @role_admin,  @p_article_read);

-- 分配 article:create, update, delete, publish 给 Author 和 Admin
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(REPLACE(UUID(), '-', ''), @role_author, @p_article_create), (REPLACE(UUID(), '-', ''), @role_admin, @p_article_create),
(REPLACE(UUID(), '-', ''), @role_author, @p_article_update), (REPLACE(UUID(), '-', ''), @role_admin, @p_article_update),
(REPLACE(UUID(), '-', ''), @role_author, @p_article_delete), (REPLACE(UUID(), '-', ''), @role_admin, @p_article_delete),
(REPLACE(UUID(), '-', ''), @role_author, @p_article_publish),(REPLACE(UUID(), '-', ''), @role_admin, @p_article_publish);

-- 分配 article:audit, top 给 Admin
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(REPLACE(UUID(), '-', ''), @role_admin, @p_article_audit),
(REPLACE(UUID(), '-', ''), @role_admin, @p_article_top);


-- =========================================================
-- 模块：评论 (Comment)
-- =========================================================
SET @p_comment_read   = REPLACE(UUID(), '-', '');
SET @p_comment_create = REPLACE(UUID(), '-', '');
SET @p_comment_delete = REPLACE(UUID(), '-', '');
SET @p_comment_audit  = REPLACE(UUID(), '-', '');

INSERT INTO `permission` (`id`, `code`, `name`, `description`) VALUES
(@p_comment_read,   'comment:read',   '阅读评论', '允许查看评论列表'),
(@p_comment_create, 'comment:create', '发表评论', '允许发表评论'),
(@p_comment_delete, 'comment:delete', '删除评论', '允许删除任意评论'),
(@p_comment_audit,  'comment:audit',  '审核评论', '允许审核/隐藏评论');

-- 分配 comment:read 给所有角色
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(REPLACE(UUID(), '-', ''), @role_guest,  @p_comment_read),
(REPLACE(UUID(), '-', ''), @role_reader, @p_comment_read),
(REPLACE(UUID(), '-', ''), @role_author, @p_comment_read),
(REPLACE(UUID(), '-', ''), @role_admin,  @p_comment_read);

-- 分配 comment:create 给 Reader, Author, Admin
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(REPLACE(UUID(), '-', ''), @role_reader, @p_comment_create),
(REPLACE(UUID(), '-', ''), @role_author, @p_comment_create),
(REPLACE(UUID(), '-', ''), @role_admin,  @p_comment_create);

-- 分配 comment:delete, audit 给 Admin
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(REPLACE(UUID(), '-', ''), @role_admin, @p_comment_delete),
(REPLACE(UUID(), '-', ''), @role_admin, @p_comment_audit);


-- =========================================================
-- 模块：分类与标签 (Category & Tag)
-- =========================================================
SET @p_category_read   = REPLACE(UUID(), '-', '');
SET @p_category_manage = REPLACE(UUID(), '-', ''); -- 包含增删改
SET @p_tag_read        = REPLACE(UUID(), '-', '');
SET @p_tag_manage      = REPLACE(UUID(), '-', '');

INSERT INTO `permission` (`id`, `code`, `name`, `description`) VALUES
(@p_category_read,   'category:read',   '阅读分类', '允许查看分类'),
(@p_category_manage, 'category:manage', '管理分类', '允许增删改分类'),
(@p_tag_read,        'tag:read',        '阅读标签', '允许查看标签'),
(@p_tag_manage,      'tag:manage',      '管理标签', '允许增删改标签');

-- read 给所有角色
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(REPLACE(UUID(), '-', ''), @role_guest,  @p_category_read), (REPLACE(UUID(), '-', ''), @role_guest,  @p_tag_read),
(REPLACE(UUID(), '-', ''), @role_reader, @p_category_read), (REPLACE(UUID(), '-', ''), @role_reader, @p_tag_read),
(REPLACE(UUID(), '-', ''), @role_author, @p_category_read), (REPLACE(UUID(), '-', ''), @role_author, @p_tag_read),
(REPLACE(UUID(), '-', ''), @role_admin,  @p_category_read), (REPLACE(UUID(), '-', ''), @role_admin,  @p_tag_read);

-- manage 给 Admin (Author 可以给 tag:create 细分，这里暂归类 manage 给 Admin，若需细分可拆)
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(REPLACE(UUID(), '-', ''), @role_admin, @p_category_manage),
(REPLACE(UUID(), '-', ''), @role_admin, @p_tag_manage);
-- 让作者也能创建标签(可选，这里暂给Admin，如果需要作者创建tag，可增加 tag:create)
SET @p_tag_create = REPLACE(UUID(), '-', '');
INSERT INTO `permission` (`id`, `code`, `name`, `description`) VALUES (@p_tag_create, 'tag:create', '创建标签', '允许创建新标签');
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(REPLACE(UUID(), '-', ''), @role_author, @p_tag_create),
(REPLACE(UUID(), '-', ''), @role_admin,  @p_tag_create);


-- =========================================================
-- 模块：文件 (File)
-- =========================================================
SET @p_file_upload = REPLACE(UUID(), '-', '');
SET @p_file_delete = REPLACE(UUID(), '-', '');

INSERT INTO `permission` (`id`, `code`, `name`, `description`) VALUES
(@p_file_upload, 'file:upload', '文件上传', '允许上传文件'),
(@p_file_delete, 'file:delete', '文件删除', '允许删除文件');

-- Author 和 Admin 可上传
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(REPLACE(UUID(), '-', ''), @role_author, @p_file_upload),
(REPLACE(UUID(), '-', ''), @role_admin,  @p_file_upload);

-- Admin 可删除
INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(REPLACE(UUID(), '-', ''), @role_admin, @p_file_delete);


-- =========================================================
-- 模块：用户与权限 (User & RBAC) - 仅限 Admin
-- =========================================================
SET @p_user_read   = REPLACE(UUID(), '-', '');
SET @p_user_update = REPLACE(UUID(), '-', ''); -- 管理员修改他人信息
SET @p_user_delete = REPLACE(UUID(), '-', ''); -- 封禁/删除
SET @p_rbac_manage = REPLACE(UUID(), '-', ''); -- 角色权限管理

INSERT INTO `permission` (`id`, `code`, `name`, `description`) VALUES
(@p_user_read,   'user:read',   '查看用户', '允许查看用户列表和详情'),
(@p_user_update, 'user:update', '管理用户', '允许修改用户信息'),
(@p_user_delete, 'user:delete', '删除用户', '允许封禁或删除用户'),
(@p_rbac_manage, 'rbac:manage', '权限管理', '允许管理角色和权限');

INSERT INTO `role_permission` (`id`, `role_id`, `permission_id`) VALUES
(REPLACE(UUID(), '-', ''), @role_admin, @p_user_read),
(REPLACE(UUID(), '-', ''), @role_admin, @p_user_update),
(REPLACE(UUID(), '-', ''), @role_admin, @p_user_delete),
(REPLACE(UUID(), '-', ''), @role_admin, @p_rbac_manage);


SET FOREIGN_KEY_CHECKS = 1;
