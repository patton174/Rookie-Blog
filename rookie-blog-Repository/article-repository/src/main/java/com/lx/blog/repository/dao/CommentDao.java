package com.lx.blog.repository.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.blog.repository.dao.impl.mapper.entity.Comment;

import java.util.List;

/**
 * @author LX
 * @date 2025/12/03
 * @description 评论数据访问接口
 */
public interface CommentDao extends IService<Comment> {

     /**
     * 统计文章评论数量
     *
     * @param articleId 文章ID
     * @return 评论数量
     */
    long countByArticle(String articleId);

    /**
     * 根据文章ID查询评论列表
     *
     * @param articleId 文章ID
     * @return 评论列表
     */
    List<Comment> listByArticle(String articleId);

    /**
     * 置顶评论
     *
     * @param commentId 评论ID
     */
    void pin(String commentId);

    /**
     * 取消置顶评论
     *
     * @param commentId 评论ID
     */
    void unpin(String commentId);

    /**
     * 获取文章当前最大楼层
     * @param articleId 文章ID
     * @return 最大楼层
     */
    Integer getMaxFloor(String articleId);
}

