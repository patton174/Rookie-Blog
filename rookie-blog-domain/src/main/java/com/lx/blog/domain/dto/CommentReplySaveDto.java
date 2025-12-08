package com.lx.blog.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author LX
 * @date 2025/12/05
 * @description 发表回复DTO
 */
@Data
public class CommentReplySaveDto {
    
    @NotNull(message = "评论ID不能为空")
    private String commentId;
    
    private String replyToReplyId; // 可空，表示回复某条具体的回复
    
    private String replyToUserId; // 可空，通常由后端根据 replyToReplyId 查询，或者前端传递
    
    @NotBlank(message = "回复内容不能为空")
    private String content;
    
    // 文章ID通常可以从 commentId 查到，但如果前端传了也可以
    private String articleId;
}
