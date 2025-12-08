package com.lx.blog.repository.dao.impl.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author LX
 * @date 2025/12/03
 * @description 文章实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("article")
public class Article {

    /**
     * 文章ID（UUID32）
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 标题
     */
    @TableField("title")
    private String title;

    /**
     * 作者ID
     */
    @TableField("author_id")
    private String authorId;

    /**
     * 状态：draft/published/archived
     */
    @TableField("status")
    private String status;

    /**
     * 摘要
     */
    @TableField("summary")
    private String summary;

    /**
     * 可见性：public/private/followers
     */
    @TableField("visibility")
    private String visibility;

    /**
     * 是否置顶
     */
    @TableField("is_top")
    private Integer isTop;

    /**
     * SEO别名
     */
    @TableField("slug")
    private String slug;

    /**
     * 封面URL
     */
    @TableField("cover_url")
    private String coverUrl;

    /**
     * 语言代码
     */
    @TableField("language")
    private String language;

    /**
     * SEO meta标题
     */
    @TableField("meta_title")
    private String metaTitle;

    /**
     * SEO meta描述
     */
    @TableField("meta_description")
    private String metaDescription;

    /**
     * 规范URL
     */
    @TableField("canonical_url")
    private String canonicalUrl;

    /**
     * 来源链接
     */
    @TableField("source_url")
    private String sourceUrl;

    /**
     * 来源类型：original/repost
     */
    @TableField("source_type")
    private String sourceType;

    /**
     * 阅读时长（分钟）
     */
    @TableField("reading_time_minutes")
    private Integer readingTimeMinutes;

    /**
     * 总字数
     */
    @TableField("word_count_total")
    private Integer wordCountTotal;

    /**
     * 是否允许评论
     */
    @TableField("allow_comment")
    private Integer allowComment;

    /**
     * 是否已审核
     */
    @TableField("is_reviewed")
    private Integer isReviewed;

    /**
     * 审核状态
     */
    @TableField("review_status")
    private String reviewStatus;

    /**
     * 审核原因
     */
    @TableField("review_reason")
    private String reviewReason;

    /**
     * 定时发布时间
     */
    @TableField("scheduled_publish_at")
    private LocalDateTime scheduledPublishAt;

    /**
     * 加精时间
     */
    @TableField("featured_at")
    private LocalDateTime featuredAt;

    /**
     * 发布时间
     */
    @TableField("publish_at")
    private LocalDateTime publishAt;

    /**
     * 内容更新时间
     */
    @TableField("update_at")
    private LocalDateTime updateAt;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;

    /**
     * 创建人
     */
    @TableField("created_by")
    private String createdBy;

    /**
     * 更新人
     */
    @TableField("updated_by")
    private String updatedBy;

    /**
     * 软删除时间
     */
    @TableField("deleted_at")
    private LocalDateTime deletedAt;
}

