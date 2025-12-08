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
 * @description 文章评论（楼主）实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment")
public class Comment {

    /**
     * 评论ID
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
     * 评论内容
     */
    @TableField("content")
    private String content;

    /**
     * 楼层序号
     */
    @TableField("floor")
    private Integer floor;

    /**
     * 是否置顶
     */
    @TableField("is_pinned")
    private Integer isPinned;

    /**
     * 是否有回复
     */
    @TableField("has_reply")
    private Integer hasReply;

    /**
     * 评论时间
     */
    @TableField("comment_at")
    private LocalDateTime commentAt;

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

