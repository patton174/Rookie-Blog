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
 * @description 评论/回复点赞与踩实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("comment_reaction")
public class CommentReaction {

    /**
     * 记录ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 文章ID（冗余）
     */
    @TableField("article_id")
    private String articleId;

    /**
     * 评论ID（楼主）
     */
    @TableField("comment_id")
    private String commentId;

    /**
     * 回复ID
     */
    @TableField("reply_id")
    private String replyId;

    /**
     * 用户ID
     */
    @TableField("user_id")
    private String userId;

    /**
     * 反应类型：like/dislike
     */
    @TableField("reaction_type")
    private String reactionType;

    /**
     * 操作时间
     */
    @TableField("react_at")
    private LocalDateTime reactAt;

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

