/*
 Navicat Premium Data Transfer

 Source Server         : rookie-blog
 Source Server Type    : MySQL
 Source Server Version : 80407
 Source Host           : 111.170.155.141:21988
 Source Schema         : rookie-blog

 Target Server Type    : MySQL
 Target Server Version : 80407
 File Encoding         : 65001

 Date: 08/12/2025 14:10:19
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for article
-- ----------------------------
DROP TABLE IF EXISTS `article`;
CREATE TABLE `article`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID，建议UUID32',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章标题',
  `author_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '作者ID（与用户模块ID一致）',
  `status` enum('draft','published','archived') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'draft' COMMENT '文章状态',
  `summary` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文章摘要',
  `visibility` enum('public','private','followers') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'public' COMMENT '可见性',
  `is_top` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否置顶',
  `slug` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SEO友好别名（唯一）',
  `cover_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '封面图URL',
  `language` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '语言代码，如 zh_CN / en_US',
  `meta_title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SEO meta标题',
  `meta_description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'SEO meta描述',
  `canonical_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '规范URL',
  `source_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源链接（转载）',
  `source_type` enum('original','repost') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'original' COMMENT '来源类型：原创/转载',
  `reading_time_minutes` int(0) NULL DEFAULT NULL COMMENT '预计阅读时长（分钟）',
  `word_count_total` int(0) NULL DEFAULT NULL COMMENT '总字数统计缓存',
  `allow_comment` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否允许评论',
  `is_reviewed` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否已审核',
  `review_status` enum('pending','approved','rejected') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'pending' COMMENT '审核状态',
  `review_reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '审核原因',
  `scheduled_publish_at` datetime(0) NULL DEFAULT NULL COMMENT '定时发布时间',
  `featured_at` datetime(0) NULL DEFAULT NULL COMMENT '加精时间',
  `publish_at` datetime(0) NULL DEFAULT NULL COMMENT '发布时间',
  `update_at` datetime(0) NULL DEFAULT NULL COMMENT '内容更新时间（与审计updated_at区分）',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  `created_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '创建人',
  `updated_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '更新人',
  `deleted_at` datetime(0) NULL DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_article_slug`(`slug`) USING BTREE,
  INDEX `idx_article_author_id`(`author_id`) USING BTREE,
  INDEX `idx_article_status_publish`(`status`, `publish_at`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章基础信息' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_attachment
-- ----------------------------
DROP TABLE IF EXISTS `article_attachment`;
CREATE TABLE `article_attachment`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '附件ID',
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `file_url` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文件URL',
  `file_type` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件类型（图片/文档等）',
  `size` bigint(0) UNSIGNED NULL DEFAULT NULL COMMENT '文件大小（字节）',
  `hash` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '文件哈希',
  `uploaded_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '上传时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_attach_article`(`article_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章附件/资源' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_category
-- ----------------------------
DROP TABLE IF EXISTS `article_category`;
CREATE TABLE `article_category`  (
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `category_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类ID',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`article_id`, `category_id`) USING BTREE,
  INDEX `fk_article_category_category`(`category_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章-分类关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_chapter
-- ----------------------------
DROP TABLE IF EXISTS `article_chapter`;
CREATE TABLE `article_chapter`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '章节ID',
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `chapter_order` int(0) NOT NULL COMMENT '章节顺序；0为虚拟章节（前言）',
  `level` tinyint(0) NOT NULL COMMENT '标题层级：0=虚拟章节，1~6对应h1~h6',
  `title` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '章节标题文本',
  `anchor` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '章节锚点（URL片段）',
  `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父章节ID（用于层级结构；虚拟章节NULL）',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物化路径（如 1/3/5）',
  `start_offset` int(0) NULL DEFAULT NULL COMMENT '在原Markdown中的开始位置',
  `end_offset` int(0) NULL DEFAULT NULL COMMENT '在原Markdown中的结束位置',
  `hash` char(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '章节内容哈希（用于变更检测）',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '创建时间',
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_chapter_article_anchor`(`article_id`, `anchor`) USING BTREE,
  INDEX `idx_chapter_article_order`(`article_id`, `chapter_order`) USING BTREE,
  INDEX `idx_chapter_article_level`(`article_id`, `level`) USING BTREE,
  INDEX `idx_chapter_parent_id`(`parent_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章章节元数据' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_content
-- ----------------------------
DROP TABLE IF EXISTS `article_content`;
CREATE TABLE `article_content`  (
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `content_md` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'Markdown内容',
  `content_html` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT 'HTML内容（渲染缓存）',
  `words_count` int(0) NULL DEFAULT NULL COMMENT '字数统计',
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0) COMMENT '更新时间',
  PRIMARY KEY (`article_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章内容' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_favorite
-- ----------------------------
DROP TABLE IF EXISTS `article_favorite`;
CREATE TABLE `article_favorite`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '收藏记录ID',
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `favorited_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '收藏时间',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_article_favorite`(`article_id`, `user_id`) USING BTREE,
  INDEX `idx_fav_user`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章收藏记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_like
-- ----------------------------
DROP TABLE IF EXISTS `article_like`;
CREATE TABLE `article_like`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '点赞记录ID',
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '用户ID',
  `liked_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '点赞时间',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_article_like`(`article_id`, `user_id`) USING BTREE,
  INDEX `idx_like_user`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章点赞记录' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_log
-- ----------------------------
DROP TABLE IF EXISTS `article_log`;
CREATE TABLE `article_log`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章操作日志ID',
  `_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '事件类',
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '用户ID（匿名为NULL）',
  `action` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '动作',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'IP地址',
  `ua` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT 'User-Agent',
  `referer` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '来源页面',
  `view_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '发生时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_article_log_article_time`(`article_id`, `view_at`) USING BTREE,
  INDEX `idx_article_log_user_article`(`user_id`, `article_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章操作日志（可分区）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_revision
-- ----------------------------
DROP TABLE IF EXISTS `article_revision`;
CREATE TABLE `article_revision`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '修订ID',
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `editor_version` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '编辑器版本',
  `content_snapshot` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL COMMENT '内容快照（整篇或章节拼接）',
  `diff_json` json NULL COMMENT '差异JSON',
  `saved_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '保存时间',
  `saved_by` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '保存人',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_revision_article_time`(`article_id`, `saved_at`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章修订/草稿' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_series
-- ----------------------------
DROP TABLE IF EXISTS `article_series`;
CREATE TABLE `article_series`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '系列ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '系列名称',
  `description` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '系列描述',
  `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '系列唯一编码',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_series_code`(`code`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章系列' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_series_item
-- ----------------------------
DROP TABLE IF EXISTS `article_series_item`;
CREATE TABLE `article_series_item`  (
  `series_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '系列ID',
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `sort_order` int(0) NOT NULL DEFAULT 0 COMMENT '系列内排序',
  PRIMARY KEY (`series_id`, `article_id`) USING BTREE,
  INDEX `idx_series_sort`(`series_id`, `sort_order`) USING BTREE,
  INDEX `fk_series_item_article`(`article_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章系列项' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_stats
-- ----------------------------
DROP TABLE IF EXISTS `article_stats`;
CREATE TABLE `article_stats`  (
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `views` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '阅读量',
  `likes` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '点赞数',
  `comments` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '评论数',
  `favorites` bigint(0) UNSIGNED NOT NULL DEFAULT 0 COMMENT '收藏数',
  `last_view_at` datetime(0) NULL DEFAULT NULL COMMENT '最后阅读时间',
  PRIMARY KEY (`article_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章统计汇总' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for article_tag
-- ----------------------------
DROP TABLE IF EXISTS `article_tag`;
CREATE TABLE `article_tag`  (
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `tag_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签ID',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`article_id`, `tag_id`) USING BTREE,
  INDEX `fk_article_tag_tag`(`tag_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章-标签关联' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类名称',
  `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '分类唯一编码',
  `parent_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '父分类ID',
  `path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '物化路径',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_category_code`(`code`) USING BTREE,
  INDEX `idx_category_parent_id`(`parent_id`) USING BTREE,
  CONSTRAINT `fk_category_parent_id` FOREIGN KEY (`parent_id`) REFERENCES `category` (`id`) ON DELETE SET NULL ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章分类' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论ID（楼主）',
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论内容',
  `floor` int(0) NOT NULL COMMENT '楼层序号',
  `is_pinned` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否置顶',
  `has_reply` tinyint(1) NOT NULL DEFAULT 0 COMMENT '是否有回复',
  `comment_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '评论时间',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `deleted_at` datetime(0) NULL DEFAULT NULL COMMENT '软删除时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_comment_article_time`(`article_id`, `comment_at`) USING BTREE,
  INDEX `idx_comment_user`(`user_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章评论（楼主）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment_reaction
-- ----------------------------
DROP TABLE IF EXISTS `comment_reaction`;
CREATE TABLE `comment_reaction`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '评论/回复点赞与踩ID',
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID（冗余）',
  `comment_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '评论ID（目标为楼主时填写）',
  `reply_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '回复ID（目标为回复时填写）',
  `user_id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '操作用户ID',
  `reaction_type` enum('like','dislike') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '反应类型：点赞或踩',
  `react_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '操作时间',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_reaction_comment`(`comment_id`, `user_id`) USING BTREE,
  UNIQUE INDEX `uk_reaction_reply`(`reply_id`, `user_id`) USING BTREE,
  INDEX `idx_reaction_user`(`user_id`) USING BTREE,
  INDEX `fk_reaction_article`(`article_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论/回复的点赞与踩' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for comment_reply
-- ----------------------------
DROP TABLE IF EXISTS `comment_reply`;
CREATE TABLE `comment_reply`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回复ID',
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID（冗余便于按文章聚合）',
  `comment_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '所属楼主评论ID',
  `reply_to_reply_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '被回复的回复ID（回复楼主时为NULL）',
  `user_id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回复者用户ID',
  `reply_to_user_id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '被回复者用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '回复内容',
  `reply_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '回复时间',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_reply_comment_time`(`comment_id`, `reply_at`) USING BTREE,
  INDEX `idx_reply_reply_to`(`reply_to_reply_id`) USING BTREE,
  INDEX `idx_reply_article`(`article_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '评论回复（两层：回复楼主或回复回复者）' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for moderation_log
-- ----------------------------
DROP TABLE IF EXISTS `moderation_log`;
CREATE TABLE `moderation_log`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '审核日志ID',
  `article_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '文章ID',
  `action` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '动作：提交/审核通过/拒绝等',
  `reason` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '原因',
  `operator_id` char(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL COMMENT '操作人ID',
  `acted_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) COMMENT '操作时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_mod_article_time`(`article_id`, `acted_at`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章审核日志' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for permission
-- ----------------------------
DROP TABLE IF EXISTS `permission`;
CREATE TABLE `permission`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_perm_code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role
-- ----------------------------
DROP TABLE IF EXISTS `role`;
CREATE TABLE `role`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `code` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `name` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_role_code`(`code`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for role_permission
-- ----------------------------
DROP TABLE IF EXISTS `role_permission`;
CREATE TABLE `role_permission`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `permission_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_role_perm`(`role_id`, `permission_id`) USING BTREE,
  INDEX `idx_role_perm_role`(`role_id`) USING BTREE,
  INDEX `idx_role_perm_perm`(`permission_id`) USING BTREE,
  CONSTRAINT `fk_role_perm_perm` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_role_perm_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签ID',
  `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签名称',
  `code` varchar(128) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL COMMENT '标签唯一编码',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_tag_code`(`code`) USING BTREE,
  INDEX `idx_tag_name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci COMMENT = '文章标签' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `username` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `status` tinyint(0) NOT NULL DEFAULT 1,
  `avatar_url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `email_verified` tinyint(0) NOT NULL DEFAULT 0,
  `email_verified_at` datetime(0) NULL DEFAULT NULL,
  `last_login_at` datetime(0) NULL DEFAULT NULL,
  `last_login_ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  `id_deleted` tinyint(0) NOT NULL DEFAULT 0,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_username`(`username`) USING BTREE,
  UNIQUE INDEX `uk_user_email`(`email`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_log
-- ----------------------------
DROP TABLE IF EXISTS `user_log`;
CREATE TABLE `user_log`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `_class` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `action` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `ip` varchar(45) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `user_agent` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `logged_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_log_user`(`user_id`) USING BTREE,
  INDEX `idx_user_log_time`(`logged_at`) USING BTREE,
  CONSTRAINT `fk_user_log_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_permission
-- ----------------------------
DROP TABLE IF EXISTS `user_permission`;
CREATE TABLE `user_permission`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `permission_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `effect` enum('grant','deny') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'grant',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_user_perm`(`user_id`, `permission_id`) USING BTREE,
  INDEX `idx_user_perm_user`(`user_id`) USING BTREE,
  INDEX `idx_user_perm_perm`(`permission_id`) USING BTREE,
  CONSTRAINT `fk_user_perm_perm` FOREIGN KEY (`permission_id`) REFERENCES `permission` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_perm_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_role
-- ----------------------------
DROP TABLE IF EXISTS `user_role`;
CREATE TABLE `user_role`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `role_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_user_role`(`user_id`, `role_id`) USING BTREE,
  INDEX `idx_user_role_user`(`user_id`) USING BTREE,
  INDEX `idx_user_role_role`(`role_id`) USING BTREE,
  CONSTRAINT `fk_user_role_role` FOREIGN KEY (`role_id`) REFERENCES `role` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_user_role_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Table structure for user_setting
-- ----------------------------
DROP TABLE IF EXISTS `user_setting`;
CREATE TABLE `user_setting`  (
  `id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `user_id` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `theme` varchar(16) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'light',
  `notify_enabled` tinyint(1) NOT NULL DEFAULT 1,
  `language` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'zh-CN',
  `timezone` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'Asia/Shanghai',
  `created_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0),
  `updated_at` datetime(0) NOT NULL DEFAULT CURRENT_TIMESTAMP(0) ON UPDATE CURRENT_TIMESTAMP(0),
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uq_user_setting_user`(`user_id`) USING BTREE,
  CONSTRAINT `fk_user_setting_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

SET FOREIGN_KEY_CHECKS = 1;
