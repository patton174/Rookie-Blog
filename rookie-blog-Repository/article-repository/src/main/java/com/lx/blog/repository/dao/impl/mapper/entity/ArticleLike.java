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
 * @description 文章点赞记录实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("article_like")
public class ArticleLike {

    /**
     * 点赞记录ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 文章ID
     */
    @TableField("article_id")
    private String articleId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 点赞时间
     */
    @TableField("liked_at")
    private LocalDateTime likedAt;

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
}

