package com.lx.blog.service.auth.biz.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.lx.blog.common.base.Result;
import com.lx.blog.common.utils.BeanCopyUtils;
import com.lx.blog.common.utils.PageUtils;
import com.lx.blog.domain.dto.CommentReactionDto;
import com.lx.blog.domain.dto.CommentReplySaveDto;
import com.lx.blog.domain.dto.CommentSaveDto;
import com.lx.blog.domain.vo.CommentReplyVo;
import com.lx.blog.domain.vo.CommentVo;
import com.lx.blog.repository.dao.CommentDao;
import com.lx.blog.repository.dao.CommentReactionDao;
import com.lx.blog.repository.dao.CommentReplyDao;
import com.lx.blog.repository.dao.impl.mapper.entity.Comment;
import com.lx.blog.repository.dao.impl.mapper.entity.CommentReply;
import com.lx.blog.service.auth.biz.ArticleCommentBizService;
import com.lx.blog.service.biz.BaseBizService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author LX
 * @date 2025/12/03
 * @description 文章评论业务服务实现类
 */
@Service
@RequiredArgsConstructor
public class ArticleCommentBizServiceImpl extends BaseBizService implements ArticleCommentBizService {

    @NotNull private final CommentDao commentDao;
    @NotNull private final CommentReplyDao replyDao;
    @NotNull private final CommentReactionDao reactionDao;

    /**
     * 统计文章评论数量
     *
     * @param articleId 文章ID
     * @return 评论数量
     */
    @Override
    public Result<Long> commentCount(String articleId) {
        long count = commentDao.countByArticle(articleId);
        return Result.ok(count);
    }

    /**
     * 查询评论列表（分页）
     *
     * @param articleId 文章ID
     * @return 评论列表
     */
    @Override
    public Result<List<CommentVo>> listComments(String articleId) {
        PageUtils.startPage();
        List<Comment> list = commentDao.listByArticle(articleId);
        PageUtils.clearPage();
        
        if (list.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }

        // 获取当前用户ID
        String userId = null;
        try { userId = StpUtil.getLoginIdAsString(); } catch (Exception ignored) {}
        
        // 批量查询当前用户对这些评论及回复的 reaction (dislike)
        // 优化：如果 userId 为空，不需要查
        Set<String> dislikedCommentIds = new java.util.HashSet<>();
        Set<String> dislikedReplyIds = new java.util.HashSet<>();
        
        if (userId != null) {
            List<String> commentIds = list.stream().map(Comment::getId).collect(Collectors.toList());
            if (!commentIds.isEmpty()) {
                dislikedCommentIds = reactionDao.getCommentIdsByReaction(commentIds, userId, "dislike");
            }
            // 预先查询回复ID有点复杂，因为回复是嵌套查询的。
            // 方案A: 循环中查 (简单，N+1) -> 暂且使用，因为分页后评论数不多
            // 方案B: 收集所有回复ID再查 -> 更好
        }

        final String currentUserId = userId;
        final Set<String> finalDislikedCommentIds = dislikedCommentIds;

        // 转换为DTO
        List<CommentVo> voList = list.stream().map(comment -> {
            CommentVo vo = BeanCopyUtils.copyProperties(comment, CommentVo.class);
            
            // 设置 isShow
            vo.setIsShow(currentUserId == null || !finalDislikedCommentIds.contains(comment.getId()));

            // TODO: 填充用户信息 (username, avatar)
            vo.setUsername("User-" + comment.getUserId()); // 模拟
            
            // 填充回复数量
            if (comment.getHasReply() != null && comment.getHasReply() == 1) {
                vo.setReplyCount(replyDao.countByCommentId(comment.getId()));
            } else {
                vo.setReplyCount(0L);
            }
            
            // 移除嵌套查询回复的逻辑
            // 前端根据 hasReply 字段判断是否需要调用 listReplies 接口
            
            return vo;
        }).collect(Collectors.toList());

        return Result.ok(voList);
    }

    /**
     * 查询评论回复列表
     *
     * @param commentId 评论ID
     * @return 回复列表
     */
    @Override
    public Result<List<CommentReplyVo>> listReplies(String commentId) {
        List<CommentReply> replies = replyDao.listByCommentId(commentId);
        if (replies.isEmpty()) {
            return Result.ok(new ArrayList<>());
        }

        // 获取当前用户ID
        String userId = null;
        try { userId = getUserId(); } catch (Exception ignored) {}

        // 批量查询回复的 dislike 状态
        Set<String> dislikedReplyIds = new java.util.HashSet<>();
        if (userId != null) {
            List<String> replyIds = replies.stream().map(CommentReply::getId).collect(Collectors.toList());
            dislikedReplyIds = reactionDao.getReplyIdsByReaction(replyIds, userId, "dislike");
        }

        final String currentUserId = userId;
        final Set<String> finalDislikedReplyIds = dislikedReplyIds;

        List<CommentReplyVo> voList = replies.stream().map(r -> {
            CommentReplyVo vo = BeanCopyUtils.copyProperties(r, CommentReplyVo.class);
            vo.setUsername("User-" + r.getUserId()); // 模拟
            if (r.getReplyToUserId() != null) {
                vo.setReplyToUsername("User-" + r.getReplyToUserId()); // 模拟
            }
            // 设置 isShow
            if (currentUserId != null && finalDislikedReplyIds.contains(r.getId())) {
                vo.setIsShow(false);
            } else {
                vo.setIsShow(true);
            }
            return vo;
        }).collect(Collectors.toList());

        return Result.ok(voList);
    }

    /**
     * 发表评论
     *
     * @param dto 评论DTO
     * @return 结果
     */
    @Override
    public Result<Object> addComment(CommentSaveDto dto) {
        Comment comment = BeanCopyUtils.copyProperties(dto, Comment.class);
        comment.setId(getId());
        comment.setUserId(getUserId());
        comment.setCommentAt(LocalDateTime.now());
        
        // 计算楼层：当前文章最大楼层 + 1
        Integer maxFloor = commentDao.getMaxFloor(comment.getArticleId());
        comment.setFloor(maxFloor != null ? maxFloor + 1 : 1);
        
        commentDao.save(comment);
        return Result.ok();
    }

    /**
     * 回复评论或回复者
     *
     * @param dto 回复DTO
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Object> addReply(CommentReplySaveDto dto) {

        CommentReply commentReply = BeanCopyUtils.copyProperties(dto, CommentReply.class);
        commentReply.setId(getId());
        commentReply.setUserId(getUserId());
        commentReply.setReplyAt(LocalDateTime.now());
        
        // 确保 articleId 存在。如果前端没传，需要先查 Comment 补全
        if (commentReply.getArticleId() == null && commentReply.getCommentId() != null) {
            Comment comment = commentDao.getById(commentReply.getCommentId());
            if (comment != null) {
                commentReply.setArticleId(comment.getArticleId());
                
                // 更新主评论的 has_reply 状态
                if (comment.getHasReply() == null || comment.getHasReply() == 0) {
                    comment.setHasReply(1);
                    commentDao.updateById(comment);
                }
            } else {
                return Result.error("评论不存在");
            }
        }
        
        replyDao.save(commentReply);
        return Result.ok();
    }

    /**
     * 评论点赞或踩
     *
     * @param dto 点赞或踩DTO
     * @return 结果
     */
    @Override
    public Result<Object> react(CommentReactionDto dto) {
        String userId = getUserId();
        if (dto.getCommentId() != null) {
            reactionDao.reactToComment(dto.getCommentId(), userId, dto.getType());
        } else if (dto.getReplyId() != null) {
            reactionDao.reactToReply(dto.getReplyId(), userId, dto.getType());
        }
        return Result.ok();
    }
}
