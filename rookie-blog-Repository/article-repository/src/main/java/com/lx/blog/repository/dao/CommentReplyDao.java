package com.lx.blog.repository.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.blog.repository.dao.impl.mapper.entity.CommentReply;

import java.util.List;

/**
 * @author LX
 * @date 2025/12/03
 * @description 评论回复数据访问接口
 */
public interface CommentReplyDao extends IService<CommentReply> {

    /**
     * 根据评论ID查询回复列表
     *
     * @param commentId 评论ID
     * @return 回复列表
     */
    List<CommentReply> listByCommentId(String commentId);

    /**
     * 根据评论ID统计回复数量
     *
     * @param commentId 评论ID
     * @return 回复数量
     */
    long countByCommentId(String commentId);
}

