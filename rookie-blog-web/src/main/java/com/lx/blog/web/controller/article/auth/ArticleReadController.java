package com.lx.blog.web.controller.article.auth;

import com.lx.blog.common.response.Result;
import com.lx.blog.domain.vo.ArticleChapterVo;
import com.lx.blog.domain.vo.ArticleContentVo;
import com.lx.blog.domain.vo.ArticleVo;
import com.lx.blog.domain.vo.TagStatVo;
import com.lx.blog.service.article.biz.ArticleReadBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author 李旭
 * @date 2025/12/03
 * @description 文章阅读查询控制器
 */
@Tag(name = "文章阅读查询控制器", description = "文章基础信息、内容、已发布文章查询")
@RestController
@RequestMapping("/article/read")
@RequiredArgsConstructor
public class ArticleReadController {

    @NotNull private final ArticleReadBizService biz;

    /**
     * 根据slug查询文章
     *
     * @param slug SEO别名
     * @return 文章信息
     */
    @GetMapping("/by-slug/{slug}")
    @Operation(summary = "根据slug查询文章", description = "根据SEO别名查询文章基础信息")
    public Result<ArticleVo> getBySlug(@PathVariable("slug") String slug) {
        return biz.getBySlug(slug);
    }

    /**
     * 根据文章Id查询文章基础信息
     *
     * @param articleId 文章ID
     * @return 文章信息
     */
    @GetMapping("/{articleId}")
    @Operation(summary = "根据文章Id查询文章基础信息", description = "根据文章ID查询文章基础信息")
    public Result<ArticleVo> getById(@PathVariable("articleId") String articleId) {
        return biz.getById(articleId);
    }

    /**
     * 查询文章内容
     *
     * @param articleId 文章ID
     * @return 文章内容
     */
    @GetMapping("/{articleId}/content")
    @Operation(summary = "查询文章内容", description = "根据文章ID查询文章内容")
    public Result<ArticleContentVo> getContent(@PathVariable("articleId") String articleId) {
        return biz.getContent(articleId);
    }

    /**
     * 查询文章章节目录
     *
     * @param articleId 文章ID
     * @return 章节列表
     */
    @GetMapping("/{articleId}/chapters")
    @Operation(summary = "查询文章章节目录", description = "根据文章ID查询文章章节目录")
    public Result<List<ArticleChapterVo>> listChapters(@PathVariable("articleId") String articleId) {
        return biz.listChapters(articleId);
    }

    /**
     * 查询已发布文章列表
     *
     * @return 文章列表
     */
    @GetMapping("/published")
    @Operation(summary = "查询已发布文章列表", description = "查询所有已发布的文章")
    public Result<List<ArticleVo>> listPublished() {
        return biz.listPublished();
    }

    /**
     * 查询用户浏览历史
     *
     * @return 文章列表
     */
    @GetMapping("/history")
    @Operation(summary = "查询用户浏览历史", description = "查询当前登录用户的浏览历史")
    public Result<List<ArticleVo>> listHistory() {
        return biz.listHistory();
    }

    /**
     * 查询热门标签
     *
     * @return 标签列表
     */
    @GetMapping("/tags/popular")
    @Operation(summary = "查询热门标签", description = "查询热门标签列表")
    public Result<List<TagStatVo>> listPopularTags() {
        return biz.listPopularTags();
    }
}


