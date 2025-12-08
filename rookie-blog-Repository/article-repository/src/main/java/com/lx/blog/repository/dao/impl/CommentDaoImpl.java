package com.lx.blog.repository.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.blog.repository.dao.CommentDao;
import com.lx.blog.repository.dao.impl.mapper.CommentMapper;
import com.lx.blog.repository.dao.impl.mapper.entity.Comment;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author LX
 * @date 2025/12/03
 * @description 评论数据访问实现
 */
@Repository
@RequiredArgsConstructor
public class CommentDaoImpl extends ServiceImpl<CommentMapper, Comment> implements CommentDao {

    @NotNull private final CommentMapper commentMapper;

    /**
     * 统计文章评论数量
     *
     * @param articleId 文章ID
     * @return 评论数量
     */
    @Override
    public long countByArticle(String articleId) {
        return commentMapper.selectCount(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId));
    }

    /**
     * 根据文章ID查询评论列表
     *
     * @param articleId 文章ID
     * @return 评论列表
     */
    @Override
    public List<Comment> listByArticle(String articleId) {
        return baseMapper.selectList(new LambdaQueryWrapper<Comment>()
                .eq(Comment::getArticleId, articleId)
                .orderByAsc(Comment::getFloor));
    }

    /**
     * 置顶评论
     *
     * @param commentId 评论ID
     */
    @Override
    public void pin(String commentId) {
        baseMapper.update(null, new LambdaUpdateWrapper<Comment>()
                .eq(Comment::getId, commentId)
                .set(Comment::getIsPinned, 1));
    }

    /**
     * 取消置顶评论
     *
     * @param commentId 评论ID
     */
    @Override
    public void unpin(String commentId) {
        baseMapper.update(null, new LambdaUpdateWrapper<Comment>()
                .eq(Comment::getId, commentId)
                .set(Comment::getIsPinned, 0));
    }

    /**
     * 获取文章当前最大楼层
     *
     * @param articleId 文章ID
     * @return 最大楼层
     */
    @Override
    public Integer getMaxFloor(String articleId) {
        // select max(floor) from comment where article_id = ?
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<Comment>()
                .select(Comment::getFloor)
                .eq(Comment::getArticleId, articleId)
                .orderByDesc(Comment::getFloor)
                .last("LIMIT 1");
        Comment c = baseMapper.selectOne(wrapper);
        return c != null ? c.getFloor() : 0;
    }
}

