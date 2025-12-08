package com.lx.blog.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author LX
 * @date 2025/12/05
 * @description 评论列表响应VO
 */
@Data
public class CommentVo {

    private String id;
    private String articleId;
    private String userId;
    private String content;
    private Integer floor;
    private Integer isPinned;
    private Integer hasReply;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime commentAt;

    // 用户信息
    private String username;
    private String avatar;

    // 是否显示（根据当前用户是否踩过决定）
    private Boolean isShow;

    // 回复数量
    private Long replyCount;
}
