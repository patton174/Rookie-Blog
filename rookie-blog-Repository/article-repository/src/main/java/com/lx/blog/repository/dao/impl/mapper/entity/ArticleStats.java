package com.lx.blog.repository.dao.impl.mapper.entity;

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
 * @description 文章统计实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("article_stats")
public class ArticleStats {

    /**
     * 文章ID（主键）
     */
    @TableId(value = "article_id")
    private String articleId;

    /**
     * 阅读量
     */
    @TableField("views")
    private Long views;

    /**
     * 点赞数
     */
    @TableField("likes")
    private Long likes;

    /**
     * 评论数
     */
    @TableField("comments")
    private Long comments;

    /**
     * 收藏数
     */
    @TableField("favorites")
    private Long favorites;

    /**
     * 最后阅读时间
     */
    @TableField("last_view_at")
    private LocalDateTime lastViewAt;
}

