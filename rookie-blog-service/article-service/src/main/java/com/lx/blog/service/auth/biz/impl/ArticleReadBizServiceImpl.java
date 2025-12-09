package com.lx.blog.service.auth.biz.impl;

import com.lx.blog.common.base.Result;
import com.lx.blog.domain.vo.ArticleChapterVo;
import com.lx.blog.domain.vo.ArticleContentVo;
import com.lx.blog.domain.vo.ArticleVo;
import com.lx.blog.domain.vo.TagStatVo;
import com.lx.blog.repository.dao.ArticleChapterDao;
import com.lx.blog.repository.dao.ArticleContentDao;
import com.lx.blog.repository.dao.ArticleDao;
import com.lx.blog.repository.dao.ArticleLogDao;
import com.lx.blog.repository.dao.TagDao;
import com.lx.blog.repository.dao.impl.mapper.entity.Article;
import com.lx.blog.repository.dao.impl.mapper.entity.Tag;
import com.lx.blog.service.auth.biz.ArticleReadBizService;
import com.lx.blog.service.biz.BaseBizService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LX
 * @date 2025/12/03
 * @description 文章阅读查询业务服务实现类
 */
@Service
@RequiredArgsConstructor
public class ArticleReadBizServiceImpl extends BaseBizService implements ArticleReadBizService {

    @NotNull private final ArticleDao articleDao;
    @NotNull private final ArticleContentDao contentDao;
    @NotNull private final ArticleChapterDao chapterDao;
    @NotNull private final ArticleLogDao viewDao;
    @NotNull private final TagDao tagDao;

    /**
     * 根据slug查询文章
     *
     * @param slug SEO别名
     * @return 文章
     */
    @Override
    public Result<ArticleVo> getBySlug(String slug) {
        return Result.ok(copyProperties(articleDao.getBySlug(slug), ArticleVo.class));
    }
    
    /**
     * 查询文章内容
     *
     * @param articleId 文章ID
     * @return 文章内容
     */
    @Override
    public Result<ArticleContentVo> getContent(String articleId) {
        return Result.ok(copyProperties(contentDao.getById(articleId), ArticleContentVo.class));
    }

    /**
     * 查询文章章节目录
     *
     * @param articleId 文章ID
     * @return 章节列表
     */
    @Override
    public Result<List<ArticleChapterVo>> listChapters(String articleId) {
        return Result.ok(copyList(chapterDao.listByArticle(articleId), ArticleChapterVo.class));
    }

    /**
     * 查询已发布文章列表（分页）
     *
     * @return 文章列表
     */
    @Override
    public Result<List<ArticleVo>> listPublished() {
        return Result.ok(getPage(articleDao::listPublished, ArticleVo.class));
    }

    /**
     * 查询用户浏览历史
     *
     * @return 文章列表
     */
    @Override
    public Result<List<ArticleVo>> listHistory() {
        List<String> articleIds = getPage(() -> viewDao.listHistoryArticleIds(getUserId()), String.class);
        if (articleIds.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }
        List<Article> articles = articleDao.listByIds(articleIds);
        List<Article> sortedArticles = new ArrayList<>();
        for (String id : articleIds) {
            articles.stream().filter(a -> a.getId().equals(id)).findFirst().ifPresent(sortedArticles::add);
        }
        return Result.ok(copyList(sortedArticles, ArticleVo.class));
    }

    /**
     * 查询热门标签
     *
     * @return 标签列表
     */
    @Override
    public Result<List<TagStatVo>> listPopularTags() {
        // 暂时查询所有标签，实际应查询热门
        List<Tag> tags = tagDao.list();
        List<TagStatVo> vos = tags.stream().map(tag -> TagStatVo.builder()
                .id(tag.getId())
                .name(tag.getName())
                .code(tag.getCode())
                .articleCount(0L) // 暂无统计
                .build()).collect(Collectors.toList());
        return Result.ok(vos);
    }

     /**
      * 根据文章Id查询文章基础信息
      *
      * @param articleId 文章ID
      * @return 文章信息
      */
    @Override
    public Result<ArticleVo> getById(String articleId) {
        return Result.ok(copyProperties(articleDao.getById(articleId), ArticleVo.class));
    }
}

