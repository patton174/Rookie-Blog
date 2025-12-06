package com.lx.blog.web.controller.article.auth;

import com.lx.blog.common.aop.log.OpLog;
import com.lx.blog.common.response.Result;
import com.lx.blog.domain.dto.ArticleViewDto;
import com.lx.blog.service.article.biz.ArticleInteractionBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author 李旭
 * @date 2025/12/03
 * @description 文章互动控制器
 */
@Tag(name = "文章互动控制器", description = "阅读记录、点赞与收藏")
@RestController
@RequestMapping("/article/interaction")
@RequiredArgsConstructor
public class ArticleInteractionController {

    @NotNull private final ArticleInteractionBizService biz;

    /**
     * 记录文章阅读
     *
     * @param articleId 文章ID
     * @param request Http请求
     * @return 是否成功
     */
    @PostMapping("/{articleId}/view")
    @Operation(summary = "记录文章阅读", description = "记录阅读明细并增加阅读统计")
    @OpLog(action = "view", func = "article.view")
    public Result<Object> appendView(@PathVariable("articleId") String articleId, HttpServletRequest request) {
        ArticleViewDto dto = ArticleViewDto.builder()
                .articleId(articleId)
                .ip(request.getRemoteAddr())
                .ua(request.getHeader("User-Agent"))
                .build();
        return biz.appendView(dto);
    }

    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @return 结果
     */
    @PostMapping("/{articleId}/like")
    @Operation(summary = "点赞文章", description = "为文章点赞")
    public Result<Object> like(@PathVariable("articleId") String articleId) {
        return biz.like(articleId);
    }

    /**
     * 取消点赞文章
     *
     * @param articleId 文章ID
     * @return 结果
     */
    @PostMapping("/{articleId}/unlike")
    @Operation(summary = "取消点赞文章", description = "取消文章点赞")
    public Result<Object> unlike(@PathVariable("articleId") String articleId) {
        return biz.unlike(articleId);
    }

    /**
     * 收藏文章
     *
     * @param articleId 文章ID
     * @return 结果
     */
    @PostMapping("/{articleId}/favorite")
    @Operation(summary = "收藏文章", description = "收藏文章")
    public Result<Object> favorite(@PathVariable("articleId") String articleId) {
        return biz.favorite(articleId);
    }

    /**
     * 取消收藏文章
     *
     * @param articleId 文章ID
     * @return 结果
     */
    @PostMapping("/{articleId}/unfavorite")
    @Operation(summary = "取消收藏文章", description = "取消收藏文章")
    public Result<Object> unfavorite(@PathVariable("articleId") String articleId) {
        return biz.unfavorite(articleId);
    }

    /**
     * 检查用户是否点赞文章
     *
     * @param articleId 文章ID
     * @return 是否点赞
     */
    @GetMapping("/{articleId}/liked")
    @Operation(summary = "检查用户是否点赞文章", description = "检查用户是否点赞文章")
    public Result<Boolean> isLiked(@PathVariable("articleId") String articleId) {
        return biz.isLiked(articleId);
    }

     /**
      * 检查用户是否收藏文章
      *
      * @param articleId 文章ID
      * @return 是否收藏
      */
    @GetMapping("/{articleId}/favorited")
    @Operation(summary = "检查用户是否收藏文章", description = "检查用户是否收藏文章")
    public Result<Boolean> isFavorited(@PathVariable("articleId") String articleId) {
        return biz.isFavorited(articleId);
    }

    /**
     * 统计文章点赞数量
     *
     * @param articleId 文章ID
     * @return 点赞数量
     */
    @GetMapping("/{articleId}/like-count")
    @Operation(summary = "统计文章点赞数量", description = "统计文章点赞数量")
    public Result<Long> likeCount(@PathVariable("articleId") String articleId) {
        return biz.likeCount(articleId);
    }

     /**
      * 统计文章收藏数量
      *
      * @param articleId 文章ID
      * @return 收藏数量
      */
    @GetMapping("/{articleId}/favorite-count")
    @Operation(summary = "统计文章收藏数量", description = "统计文章收藏数量")
    public Result<Long> favoriteCount(@PathVariable("articleId") String articleId) {
        return biz.favoriteCount(articleId);
    }
}

