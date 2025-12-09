package com.lx.blog.service.auth.biz;

import com.lx.blog.common.base.Result;
import com.lx.blog.domain.dto.CommentReactionDto;
import com.lx.blog.domain.dto.CommentReplySaveDto;
import com.lx.blog.domain.dto.CommentSaveDto;
import com.lx.blog.domain.vo.CommentReplyVo;
import com.lx.blog.domain.vo.CommentVo;

import java.util.List;

/**
 * @author LX
 * @date 2025/12/03
 * @description 文章评论业务服务接口
 */
public interface ArticleCommentBizService {

     /**
     * 统计文章评论数量
      *
     * @param articleId 文章ID
     * @return 评论数量
     */
    Result<Long> commentCount(String articleId);

    /**
     * 查询评论列表（分页）
     *
     * @param articleId 文章ID
     * @return 评论列表
     */
    Result<List<CommentVo>> listComments(String articleId);

    /**
     * 查询评论回复列表
     *
     * @param commentId 评论ID
     * @return 回复列表
     */
    Result<List<CommentReplyVo>> listReplies(String commentId);

    /**
     * 添加评论
     *
     * @param dto 评论DTO
     * @return 结果
     */
    Result<Object> addComment(CommentSaveDto dto);

     /**
     * 添加评论回复
     *
     * @param dto 回复DTO
     * @return 结果
     */
    Result<Object> addReply(CommentReplySaveDto dto);

    /**
     * 评论或回复点赞或取消点赞
     *
     * @param dto 点赞或踩DTO
     * @return 结果
     */
    Result<Object> react(CommentReactionDto dto);
}

