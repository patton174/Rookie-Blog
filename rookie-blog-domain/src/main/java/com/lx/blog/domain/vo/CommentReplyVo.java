package com.lx.blog.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author LX
 * @date 2025/12/05
 * @description 评论回复响应VO
 */
@Data
public class CommentReplyVo {

    private String id;
    private String articleId;
    private String commentId;
    private String replyToReplyId;
    private String userId;
    private String replyToUserId;
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime replyAt;

    // 用户信息
    private String username;
    private String avatar;
    private String replyToUsername;

    // 是否显示（根据当前用户是否踩过决定）
    private Boolean isShow;
}
