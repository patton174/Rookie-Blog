package com.lx.blog.web.controller.article.auth;import com.lx.blog.common.base.OpLog;
import com.lx.blog.common.base.Result;
import com.lx.blog.domain.dto.CommentReactionDto;
import com.lx.blog.domain.dto.CommentReplySaveDto;
import com.lx.blog.domain.dto.CommentSaveDto;
import com.lx.blog.domain.vo.CommentReplyVo;
import com.lx.blog.domain.vo.CommentVo;
import com.lx.blog.service.auth.biz.ArticleCommentBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author LX
 * @date 2025/12/03
 * @description 文章评论控制器
 */
@Tag(name = "文章评论控制器", description = "评论列表、发表评论、回复与点赞踩")
@RestController
@RequestMapping("/article/comment")
@RequiredArgsConstructor
public class ArticleCommentController {

    @NotNull private final ArticleCommentBizService biz;

    /**
     * 统计文章评论数量
     *
     * @param articleId 文章ID
     * @return 评论数量
     */
    @GetMapping("/{articleId}/comment-count")
    @Operation(summary = "统计文章评论数量", description = "统计文章下的评论数量（不含回复）")
    public Result<Long> commentCount(@PathVariable("articleId") String articleId) {
        return biz.commentCount(articleId);
    }

    /**
     * 查询评论列表
     *
     * @param articleId 文章ID
     * @return 评论列表
     */
    @GetMapping("/{articleId}/comments")
    @Operation(summary = "查询评论列表", description = "查询文章的评论列表（不含回复）")
    public Result<List<CommentVo>> listComments(@PathVariable("articleId") String articleId) {
        return biz.listComments(articleId);
    }

    /**
     * 查询评论回复列表
     *
     * @param commentId 评论ID
     * @return 回复列表
     */
    @GetMapping("/{commentId}/replies")
    @Operation(summary = "查询评论回复列表", description = "查询指定评论的回复列表")
    public Result<List<CommentReplyVo>> listReplies(@PathVariable("commentId") String commentId) {
        return biz.listReplies(commentId);
    }

    /**
     * 发表评论
     *
     * @param dto 评论信息
     * @return 结果
     */
    @PostMapping("/add")
    @OpLog(action = "add", func = "article.comment")
    @Operation(summary = "发表评论", description = "在文章下发表评论")
    public Result<Object> addComment(@RequestBody @Valid CommentSaveDto dto) {
        return biz.addComment(dto);
    }

    /**
     * 回复评论或回复者
     *
     * @param dto 回复信息
     * @return 结果
     */
    @PostMapping("/reply")
    @OpLog(action = "reply", func = "article.comment")
    @Operation(summary = "回复评论", description = "回复楼主或某条回复")
    public Result<Object> addReply(@RequestBody @Valid CommentReplySaveDto dto) {
        return biz.addReply(dto);
    }

    /**
     * 评论点赞或踩
     *
     * @param dto 点赞或踩DTO
     * @return 结果
     */
    @PostMapping("/reaction")
    @OpLog(action = "react", func = "article.comment")
    @Operation(summary = "评论点赞或踩", description = "对评论或回复进行点赞或踩")
    public Result<Object> react(@RequestBody @Valid CommentReactionDto dto) {
        return biz.react(dto);
    }
}

