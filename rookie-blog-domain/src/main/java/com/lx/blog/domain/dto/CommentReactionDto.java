package com.lx.blog.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author 李旭
 * @date 2025/12/6
 * @description 评论点赞或踩DTO
 */
@Data
public class CommentReactionDto {

    /**
     * 评论ID（可空）
     */
    private Long commentId;

    /**
     * 回复ID（可空）
     */
    private Long replyId;

    /**
     * 操作类型（like/dislike）
     */
    @NotNull(message = "操作类型不能为空")
    private String type;
}
