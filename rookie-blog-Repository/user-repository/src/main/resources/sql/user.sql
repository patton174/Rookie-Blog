-- 用户与权限相关表结构（MySQL 8.0，InnoDB，utf8mb4）

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS `role_permission`;
DROP TABLE IF EXISTS `user_permission`;
DROP TABLE IF EXISTS `user_role`;
DROP TABLE IF EXISTS `login_logs`;
DROP TABLE IF EXISTS `user_setting`;
DROP TABLE IF EXISTS `permission`;
DROP TABLE IF EXISTS `role`;
DROP TABLE IF EXISTS `user`;

CREATE TABLE `user` (
  `id` CHAR(32) NOT NULL,
  `username` VARCHAR(64) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `email` VARCHAR(64) NOT NULL,
  `status` TINYINT NOT NULL DEFAULT 1,
  `avatar_url` VARCHAR(255) DEFAULT NULL,
  `email_verified` TINYINT NOT NULL DEFAULT 0,
  `email_verified_at` DATETIME DEFAULT NULL,
  `last_login_at` DATETIME DEFAULT NULL,
  `last_login_ip` VARCHAR(45) DEFAULT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `id_deleted` TINYINT NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_username` (`username`),
  UNIQUE KEY `uk_user_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(64) NOT NULL,
  `name` VARCHAR(128) NOT NULL,
  `description` VARCHAR(255),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_role_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(128) NOT NULL,
  `name` VARCHAR(128) NOT NULL,
  `description` VARCHAR(255),
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_perm_code` (`code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_role` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` CHAR(32) NOT NULL,
  `role_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_role` (`user_id`,`role_id`),
  KEY `idx_user_role_user` (`user_id`),
  KEY `idx_user_role_role` (`role_id`),
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `role_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `role_id` BIGINT NOT NULL,
  `permission_id` BIGINT NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_role_perm` (`role_id`,`permission_id`),
  KEY `idx_role_perm_role` (`role_id`),
  KEY `idx_role_perm_perm` (`permission_id`),
  CONSTRAINT `fk_role_perm_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_perm_perm` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_permission` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` CHAR(32) NOT NULL,
  `permission_id` BIGINT NOT NULL,
  `effect` ENUM('grant','deny') NOT NULL DEFAULT 'grant',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_perm` (`user_id`,`permission_id`),
  KEY `idx_user_perm_user` (`user_id`),
  KEY `idx_user_perm_perm` (`permission_id`),
  CONSTRAINT `fk_user_perm_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_perm_perm` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_log` (
  `id` VARCHAR(32) NOT NULL,
  `_class` VARCHAR(255) DEFAULT NULL,
  `user_id` CHAR(32) DEFAULT NULL,
  `action` VARCHAR(64) DEFAULT NULL,
  `ip` VARCHAR(45) DEFAULT NULL,
  `user_agent` VARCHAR(255) DEFAULT NULL,
  `logged_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  KEY `idx_user_log_user` (`user_id`),
  KEY `idx_user_log_time` (`logged_at`),
  CONSTRAINT `fk_user_log_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `user_setting` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` CHAR(32) NOT NULL,
  `theme` VARCHAR(16) DEFAULT 'light',
  `notify_enabled` TINYINT(1) NOT NULL DEFAULT 1,
  `language` VARCHAR(32) DEFAULT 'zh-CN',
  `timezone` VARCHAR(64) DEFAULT 'Asia/Shanghai',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uq_user_setting_user` (`user_id`),
  CONSTRAINT `fk_user_setting_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 基础种子数据
INSERT INTO `role` (`code`,`name`,`description`) VALUES
('ADMIN','管理员','系统管理'),
('EDITOR','编辑','内容编辑与审核'),
('AUTHOR','作者','创作与自我管理'),
('READER','读者','阅读与评论');

INSERT INTO `permission` (`code`,`name`,`description`) VALUES
('post.create','创建文章','创建博客文章'),
('post.edit','编辑文章','编辑自己的文章'),
('post.delete','删除文章','删除文章'),
('post.publish','发布文章','发布文章到站点'),
('comment.create','发表评论','发表评论'),
('comment.delete','删除评论','删除评论'),
('comment.moderate','评论审核','审核和屏蔽评论'),
('user.manage','用户管理','管理用户与权限');

-- 角色权限映射
INSERT INTO `role_permission` (`role_id`,`permission_id`,`created_at`)
SELECT r.id, p.id, NOW() FROM `role` r JOIN `permission` p ON 1=1 WHERE r.`code`='ADMIN';

INSERT INTO `role_permission` (`role_id`,`permission_id`,`created_at`)
SELECT r.id, p.id, NOW() FROM `role` r JOIN `permission` p ON p.`code` IN ('post.edit','post.publish','comment.moderate') WHERE r.`code`='EDITOR';

INSERT INTO `role_permission` (`role_id`,`permission_id`,`created_at`)
SELECT r.id, p.id, NOW() FROM `role` r JOIN `permission` p ON p.`code` IN ('post.create','post.edit','comment.create') WHERE r.`code`='AUTHOR';

INSERT INTO `role_permission` (`role_id`,`permission_id`,`created_at`)
SELECT r.id, p.id, NOW() FROM `role` r JOIN `permission` p ON p.`code` IN ('comment.create') WHERE r.`code`='READER';

SET FOREIGN_KEY_CHECKS = 1;
