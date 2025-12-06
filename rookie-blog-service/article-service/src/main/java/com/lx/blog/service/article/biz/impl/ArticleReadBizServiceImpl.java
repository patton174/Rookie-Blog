package com.lx.blog.service.article.biz.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.lx.blog.common.response.Result;
import com.lx.blog.common.utils.BeanCopyUtils;
import com.lx.blog.common.utils.PageUtils;
import com.lx.blog.domain.vo.ArticleChapterVo;
import com.lx.blog.domain.vo.ArticleContentVo;
import com.lx.blog.domain.vo.ArticleVo;
import com.lx.blog.domain.vo.TagStatVo;
import com.lx.blog.repository.dao.ArticleChapterDao;
import com.lx.blog.repository.dao.ArticleContentDao;
import com.lx.blog.repository.dao.ArticleDao;
import com.lx.blog.repository.dao.ArticleViewDao;
import com.lx.blog.repository.dao.TagDao;
import com.lx.blog.repository.dao.impl.mapper.entity.Article;
import com.lx.blog.repository.dao.impl.mapper.entity.ArticleChapter;
import com.lx.blog.repository.dao.impl.mapper.entity.ArticleContent;
import com.lx.blog.repository.dao.impl.mapper.entity.Tag;
import com.lx.blog.service.article.biz.ArticleReadBizService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author 李旭
 * @date 2025/12/03
 * @description 文章阅读查询业务服务实现类
 */
@Service
@RequiredArgsConstructor
public class ArticleReadBizServiceImpl implements ArticleReadBizService {

    @NotNull private final ArticleDao articleDao;
    @NotNull private final ArticleContentDao contentDao;
    @NotNull private final ArticleChapterDao chapterDao;
    @NotNull private final ArticleViewDao viewDao;
    @NotNull private final TagDao tagDao;

    /**
     * 根据slug查询文章
     *
     * @param slug SEO别名
     * @return 文章
     */
    @Override
    public Result<ArticleVo> getBySlug(String slug) {
        Article article = articleDao.getBySlug(slug);
        return Result.ok(BeanCopyUtils.copyProperties(article, ArticleVo.class));
    }
    
    /**
     * 查询文章内容
     *
     * @param articleId 文章ID
     * @return 文章内容
     */
    @Override
    public Result<ArticleContentVo> getContent(String articleId) {
        ArticleContent content = contentDao.getById(articleId);
        return Result.ok(BeanCopyUtils.copyProperties(content, ArticleContentVo.class));
    }

    /**
     * 查询文章章节目录
     *
     * @param articleId 文章ID
     * @return 章节列表
     */
    @Override
    public Result<List<ArticleChapterVo>> listChapters(String articleId) {
        List<ArticleChapter> chapters = chapterDao.listByArticle(articleId);
        return Result.ok(BeanCopyUtils.copyList(chapters, ArticleChapterVo.class));
    }

    /**
     * 查询已发布文章列表（分页）
     *
     * @return 文章列表
     */
    @Override
    public Result<List<ArticleVo>> listPublished() {
        PageUtils.startPage();
        List<Article> list = articleDao.listPublished();
        PageUtils.clearPage();
        return Result.ok(BeanCopyUtils.copyList(list, ArticleVo.class));
    }

    /**
     * 查询用户浏览历史
     *
     * @return 文章列表
     */
    @Override
    public Result<List<ArticleVo>> listHistory() {
        String userId = StpUtil.getLoginIdAsString();
        PageUtils.startPage();
        List<String> articleIds = viewDao.listHistoryArticleIds(userId);
        PageUtils.clearPage();

        if (articleIds.isEmpty()) {
            return Result.ok(Collections.emptyList());
        }

        // 批量查询文章信息
        // 注意：selectBatchIds 不保证顺序，如果需要按浏览时间排序，需要在内存中重排或手动拼SQL
        // 这里简单实现：查出来后按 IDs 顺序重排
        List<Article> articles = articleDao.listByIds(articleIds);
        
        // 重排
        List<Article> sortedArticles = new ArrayList<>();
        for (String id : articleIds) {
            articles.stream().filter(a -> a.getId().equals(id)).findFirst().ifPresent(sortedArticles::add);
        }

        return Result.ok(BeanCopyUtils.copyList(sortedArticles, ArticleVo.class));
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
        List<TagStatVo> vos = tags.stream().map(tag -> {
            TagStatVo vo = new TagStatVo();
            vo.setId(tag.getId());
            vo.setName(tag.getName());
            vo.setCode(tag.getCode());
            vo.setArticleCount(0L); // 暂无统计
            return vo;
        }).collect(Collectors.toList());
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
        Article article = articleDao.getById(articleId);
        return Result.ok(BeanCopyUtils.copyProperties(article, ArticleVo.class));
    }
}

