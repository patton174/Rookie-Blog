package com.lx.blog.repository.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.blog.repository.dao.CommentReplyDao;
import com.lx.blog.repository.dao.impl.mapper.CommentReplyMapper;
import com.lx.blog.repository.dao.impl.mapper.entity.CommentReply;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author LX
 * @date 2025/12/03
 * @description 评论回复数据访问实现
 */
@Repository
@RequiredArgsConstructor
public class CommentReplyDaoImpl extends ServiceImpl<CommentReplyMapper, CommentReply> implements CommentReplyDao {

    /**
     * 根据评论ID查询回复列表
     *
     * @param commentId 评论ID
     * @return 回复列表
     */
    @Override
    public List<CommentReply> listByCommentId(String commentId) {
        return baseMapper.selectList(new LambdaQueryWrapper<CommentReply>()
                .eq(CommentReply::getCommentId, commentId)
                .orderByAsc(CommentReply::getReplyAt));
    }

    /**
     * 根据评论ID统计回复数量
     *
     * @param commentId 评论ID
     * @return 回复数量
     */
    @Override
    public long countByCommentId(String commentId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<CommentReply>()
                .eq(CommentReply::getCommentId, commentId));
    }
}

