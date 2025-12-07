package com.lx.blog.web.controller.article.auth;

import com.lx.blog.common.aop.log.OpLog;
import com.lx.blog.common.response.Result;
import com.lx.blog.domain.dto.ArticleSaveDto;
import com.lx.blog.domain.vo.UserArticleStatsVo;
import com.lx.blog.service.article.biz.ArticleBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * @author LX
 * @date 2025/12/03
 * @description 文章管理控制器
 */
@Tag(name = "文章管理控制器", description = "文章发布、草稿、删除等管理操作")
@RestController
@RequestMapping("/article")
@RequiredArgsConstructor
public class ArticleController {

    @NotNull private final ArticleBizService biz;

    /**
     * 保存草稿
     *
     * @param dto 文章信息
     * @return 文章ID
     */
    @PostMapping("/draft")
    @Operation(summary = "保存草稿", description = "保存文章为草稿")
    @OpLog(action = "save_draft", func = "article.draft")
    public Result<String> saveDraft(@RequestBody ArticleSaveDto dto) {
        return biz.saveDraft(dto);
    }

    /**
     * 发布文章
     *
     * @param dto 文章信息
     * @return 文章ID
     */
    @PostMapping("/publish")
    @Operation(summary = "发布文章", description = "发布文章")
    @OpLog(action = "publish_article", func = "article.publish")
    public Result<Map<String, String>> publish(@RequestBody ArticleSaveDto dto) {
        return biz.publish(dto);
    }

    /**
     * 判断是否为当前用户的文章
     *
     * @param articleId 文章ID
     * @return 是否为当前用户的文章
     */
    @GetMapping("/{articleId}/check")
    @Operation(summary = "检查文章是否为当前用户", description = "根据ID检查文章是否为当前登录用户的文章")
    public Result<Boolean> checkOwnership(@PathVariable("articleId") String articleId) {
        return biz.checkOwnership(articleId);
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "删除文章", description = "根据ID删除文章（软删除）")
    @OpLog(action = "delete_article", func = "article.delete")
    public Result<Boolean> delete(@PathVariable("id") String id) {
        return biz.delete(id);
    }

    /**
     * 获取当前用户文章统计
     *
     * @return 统计信息
     */
    @GetMapping("/stats/me")
    @Operation(summary = "获取当前用户文章统计", description = "获取当前登录用户的文章统计信息")
    public Result<UserArticleStatsVo> getMyStats() {
        return biz.getMyStats();
    }

    /**
     * 获取指定用户文章统计
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    @GetMapping("/stats/{userId}")
    @Operation(summary = "获取指定用户文章统计", description = "获取指定用户的文章统计信息")
    public Result<UserArticleStatsVo> getUserStats(@PathVariable("userId") String userId) {
        return biz.getUserStats(userId);
    }
}

