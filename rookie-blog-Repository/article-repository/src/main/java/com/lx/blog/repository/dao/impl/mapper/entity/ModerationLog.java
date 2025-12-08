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
 * @description 审核日志实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("moderation_log")
public class ModerationLog {

    /**
     * 审核日志ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 文章ID
     */
    @TableField("article_id")
    private String articleId;

    /**
     * 动作
     */
    @TableField("action")
    private String action;

    /**
     * 原因
     */
    @TableField("reason")
    private String reason;

    /**
     * 操作人ID
     */
    @TableField("operator_id")
    private String operatorId;

    /**
     * 操作时间
     */
    @TableField("acted_at")
    private LocalDateTime actedAt;
}

