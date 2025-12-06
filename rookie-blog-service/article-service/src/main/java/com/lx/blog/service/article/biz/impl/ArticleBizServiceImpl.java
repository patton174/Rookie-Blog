package com.lx.blog.service.article.biz.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lx.blog.common.response.Result;
import com.lx.blog.domain.dto.ArticleSaveDto;
import com.lx.blog.domain.vo.UserArticleStatsVo;
import com.lx.blog.repository.dao.*;
import com.lx.blog.repository.dao.impl.mapper.ArticleCategoryMapper;
import com.lx.blog.repository.dao.impl.mapper.ArticleTagMapper;
import com.lx.blog.repository.dao.impl.mapper.entity.*;
import com.lx.blog.service.article.biz.ArticleBizService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 李旭
 * @date 2025/12/03
 * @description 文章业务服务实现类
 */
@Service
@RequiredArgsConstructor
public class ArticleBizServiceImpl implements ArticleBizService {

    @NotNull private final ArticleDao articleDao;
    @NotNull private final ArticleRevisionDao revisionDao;
    @NotNull private final ArticleContentDao contentDao;
    @NotNull private final ArticleChapterDao chapterDao;
    @NotNull private final ArticleStatsDao statsDao;
    @NotNull private final CategoryDao categoryDao;
    @NotNull private final TagDao tagDao;
    @NotNull private final ArticleCategoryMapper articleCategoryMapper;
    @NotNull private final ArticleTagMapper articleTagMapper;

    /**
     * 保存草稿
     *
     * @param dto 文章信息
     * @return 文章ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> saveDraft(ArticleSaveDto dto) {
        String articleId = saveArticleBase(dto, "draft");
        saveRevision(articleId, dto.getContentMd());
        return Result.ok(articleId);
    }

    /**
     * 发布文章
     *
     * @param dto 文章信息
     * @return 文章ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> publish(ArticleSaveDto dto) {
        String articleId = saveArticleBase(dto, "published");
        saveRevision(articleId, dto.getContentMd());
        // 保存内容
        saveContent(articleId, dto.getContentMd(), dto.getContentHtml());
        // 解析并保存章节（目录）
        parseAndSaveChapters(articleId, dto.getContentMd());
        // 保存关联
        saveRelations(articleId, dto);

        return Result.ok(articleId);
    }

    /**
     * 删除文章
     *
     * @param id 文章ID
     * @return 是否成功
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<Boolean> delete(String id) {
        // 软删除
        Article article = articleDao.getById(id);
        if (article != null) {
            article.setDeletedAt(LocalDateTime.now());
            articleDao.updateById(article);
        }
        return Result.ok(true);
    }

    /**
     * 判断是否为当前用户的文章
     *
     * @param articleId 文章ID
     * @return 是否为当前用户的文章
     */
    @Override
    public Result<Boolean> checkOwnership(String articleId) {
        Article article = articleDao.getById(articleId);
        if (article == null) {
            return Result.ok(false);
        }
        return Result.ok(article.getAuthorId().equals(StpUtil.getLoginIdAsString()));
    }

    /**
     * 获取当前用户文章统计
     *
     * @return 统计信息
     */
    @Override
    public Result<UserArticleStatsVo> getMyStats() {
        return getUserStats(StpUtil.getLoginIdAsString());
    }

    /**
     * 获取指定用户文章统计
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    @Override
    public Result<UserArticleStatsVo> getUserStats(String userId) {
        Long count = articleDao.countByAuthorId(userId);
        
        // 暂无总浏览量、总点赞量统计，预留接口
        // 实际应查询 article join article_stats sum(views), sum(likes)
        
        return Result.ok(UserArticleStatsVo.builder()
                .articleCount(count)
                .viewCount(0L) // TODO
                .likeCount(0L) // TODO
                .build());
    }

    /**
     * 保存文章基础信息（草稿或发布）
     *
     * @param dto 文章信息
     * @param status 文章状态（draft或published）
     * @return 文章ID
     */
    private String saveArticleBase(ArticleSaveDto dto, String status) {
        Article article;
        boolean isNew = false;
        if (StringUtils.hasText(dto.getId())) {
            article = articleDao.getById(dto.getId());
            if (article == null) {
                throw new RuntimeException("文章不存在");
            }
        } else {
            article = new Article();
            isNew = true;
        }

        article.setTitle(dto.getTitle());
        article.setSummary(dto.getSummary());
        article.setStatus(status);
        article.setCoverUrl(dto.getCoverUrl());
        article.setAllowComment(dto.getAllowComment() != null ? dto.getAllowComment() : 1);
        article.setUpdateAt(LocalDateTime.now());
        
        if (isNew) {
            article.setAuthorId(StpUtil.getLoginIdAsString());
            article.setCreatedAt(LocalDateTime.now());
            article.setIsReviewed(0); // 默认未审核
            // 生成Slug (简单处理，实际应用可能需要更复杂的逻辑)
            if (!StringUtils.hasText(article.getSlug())) {
                article.setSlug(UUID.randomUUID().toString().substring(0, 8)); 
            }
            articleDao.save(article);
            
            // 初始化统计
            ArticleStats stats = ArticleStats.builder()
                    .articleId(article.getId())
                    .views(0L).likes(0L).comments(0L).favorites(0L)
                    .build();
            statsDao.save(stats);
        } else {
            articleDao.updateById(article);
        }
        
        if ("published".equals(status)) {
            article.setPublishAt(LocalDateTime.now());
            articleDao.updateById(article);
        }
        
        return article.getId();
    }

    /**
     * 保存文章修订版本
     *
     * @param articleId 文章ID
     * @param contentMd 文章内容（Markdown格式）
     */
    private void saveRevision(String articleId, String contentMd) {
        ArticleRevision revision = ArticleRevision.builder()
                .articleId(articleId)
                .contentSnapshot(contentMd)
                .savedAt(LocalDateTime.now())
                .savedBy(StpUtil.getLoginIdAsString())
                .build();
        revisionDao.save(revision);
    }

    /**
     * 保存文章内容
     *
     * @param articleId 文章ID
     * @param contentMd 文章内容（Markdown格式）
     * @param contentHtml 文章内容（HTML格式）
     */
    private void saveContent(String articleId, String contentMd, String contentHtml) {
        ArticleContent contentEntity = contentDao.getById(articleId);
        if (contentEntity == null) {
            contentEntity = ArticleContent.builder()
                    .articleId(articleId)
                    .build();
        }
        contentEntity.setContentMd(contentMd);
        contentEntity.setContentHtml(contentHtml);
        contentEntity.setWordsCount(contentMd != null ? contentMd.length() : 0);
        contentEntity.setUpdatedAt(LocalDateTime.now());
        
        contentDao.saveOrUpdate(contentEntity);
    }

    /**
     * 保存文章关联关系（分类、标签）
     *
     * @param articleId 文章ID
     * @param dto 文章保存DTO，包含分类编码和标签列表
     */
    private void saveRelations(String articleId, ArticleSaveDto dto) {
        // 1. Category
        articleCategoryMapper.delete(new LambdaQueryWrapper<ArticleCategory>().eq(ArticleCategory::getArticleId, articleId));
        if (StringUtils.hasText(dto.getCategoryCode())) {
            Category cat = categoryDao.getByCode(dto.getCategoryCode());
            if (cat != null) {
                ArticleCategory ac = ArticleCategory.builder()
                        .articleId(articleId)
                        .categoryId(cat.getId())
                        .createdAt(LocalDateTime.now())
                        .build();
                articleCategoryMapper.insert(ac);
            }
        }

        // 2. Tags
        articleTagMapper.delete(new LambdaQueryWrapper<ArticleTag>().eq(ArticleTag::getArticleId, articleId));
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            for (String tagName : dto.getTags()) {
                 Tag tag = tagDao.getByCode(tagName);
                 if (tag != null) {
                     ArticleTag at = ArticleTag.builder()
                             .articleId(articleId)
                             .tagId(tag.getId())
                             .createdAt(LocalDateTime.now())
                             .build();
                     articleTagMapper.insert(at);
                 }
            }
        }
    }

    /**
     * 解析并保存章节目录
     * 简单实现：提取 Markdown 中的 h1-h6 标题
     */
    private void parseAndSaveChapters(String articleId, String content) {
        // 1. 清理旧章节
        List<ArticleChapter> oldChapters = chapterDao.listByArticle(articleId);
        for (ArticleChapter ch : oldChapters) {
            chapterDao.removeById(ch.getId());
        }

        if (!StringUtils.hasText(content)) {
            return;
        }

        // 2. 解析新章节
        // 正则匹配：^#{1,6}\s+(.*)$
        Pattern pattern = java.util.regex.Pattern.compile("^(#{1,6})\\s+(.*)$", java.util.regex.Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(content);

        ArrayDeque<ArticleChapter> stack = new ArrayDeque<>();
        HashMap<String, Integer> anchorCount = new HashMap<>();
        int order = 1;
        while (matcher.find()) {
            String hashes = matcher.group(1);
            String title = matcher.group(2).trim();
            int level = hashes.length();
            int start = matcher.start();
            int end = matcher.end();

            String base = slugify(title);
            Integer cnt = anchorCount.getOrDefault(base, 0);
            String anchor = cnt == 0 ? base : base + "-" + cnt;
            anchorCount.put(base, cnt + 1);

            while (!stack.isEmpty() && stack.peek().getLevel() >= level) {
                stack.pop();
            }
            Long parentId = stack.isEmpty() ? null : stack.peek().getId();
            String parentPath = stack.isEmpty() ? null : stack.peek().getPath();

            ArticleChapter chapter = ArticleChapter.builder()
                    .articleId(articleId)
                    .chapterOrder(order++)
                    .level(level)
                    .title(title)
                    .anchor(anchor)
                    .parentId(parentId)
                    .startOffset(start)
                    .endOffset(end)
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            chapterDao.save(chapter);
            String path = parentPath != null ? parentPath + "/" + chapter.getId() : String.valueOf(chapter.getId());
            chapter.setPath(path);
            chapterDao.updateById(chapter);
            stack.push(chapter);
        }
    }

    private String slugify(String s) {
        String t = s == null ? "" : s;
        t = t.trim().toLowerCase();
        t = t.replaceAll("[\\p{Z}]+", " ");
        t = t.replaceAll("[\\p{Punct}]", " ");
        t = t.replaceAll("\\s+", "-");
        t = t.replaceAll("-+", "-");
        t = t.replaceAll("^-|-$", "");
        if (t.isEmpty()) {
            t = "section";
        }
        if (t.length() > 64) {
            t = t.substring(0, 64);
        }
        return t;
    }
}

