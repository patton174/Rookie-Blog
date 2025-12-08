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
 * @description 评论回复（两层）实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@TableName("comment_reply")
public class CommentReply {

    /**
     * 回复ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 文章ID
     */
    @TableField("article_id")
    private String articleId;

    /**
     * 所属楼主评论ID
     */
    @TableField("comment_id")
    private String commentId;

    /**
     * 被回复的回复ID（回复楼主为NULL）
     */
    @TableField("reply_to_reply_id")
    private String replyToReplyId;

    /**
     * 回复者用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 被回复者用户ID
     */
    @TableField("reply_to_user_id")
    private String replyToUserId;

    /**
     * 回复内容
     */
    @TableField("content")
    private String content;

    /**
     * 回复时间
     */
    @TableField("reply_at")
    private LocalDateTime replyAt;

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

