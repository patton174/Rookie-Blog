package com.lx.blog.service.article.biz.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lx.blog.common.response.Result;
import com.lx.blog.common.utils.UUIDUtils;
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
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author LX
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
    public Result<Map<String, String>> publish(ArticleSaveDto dto) {
        String articleId = saveArticleBase(dto, "published");
        saveRevision(articleId, dto.getContentMd());
        // 保存内容
        saveContent(articleId, dto.getContentMd(), dto.getContentHtml());
        // 解析并保存章节（目录）
        parseAndSaveChapters(articleId, dto.getContentMd());
        // 保存关联
        saveRelations(articleId, dto);

        Map<String, String> result = new HashMap<>();
        result.put("articleId", articleId);
        return Result.ok(result);
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
     *
     * @param articleId 文章ID
     * @param content 文章内容
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

        // 2. 预处理内容：保护代码块
        String processedContent = protectCodeBlocks(content);

        // 3. 解析新章节
        // 使用更精确的正则表达式，只匹配真正的标题行
        Pattern pattern = Pattern.compile("^([#]{1,6})\\s+(.+)$", Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(processedContent);

        ArrayDeque<ArticleChapter> stack = new ArrayDeque<>();
        HashMap<String, Integer> anchorCount = new HashMap<>();
        int order = 1;
        List<ArticleChapter> chapters = new ArrayList<>();

        while (matcher.find()) {
            String hashes = matcher.group(1);
            String rawTitle = matcher.group(2).trim();
            int level = hashes.length();
            int start = matcher.start();

            // 检查是否在代码块内（如果是占位符，则跳过）
            if (isInsideCodeBlock(matcher.group(0), processedContent, start)) {
                continue;
            }

            // 提取纯文本标题，去除HTML标签
            String title = extractPlainTextFromTitle(rawTitle);

            // 清理标题
            title = cleanTitleText(title);

            if (title.isEmpty()) {
                continue;
            }

            String base = slugify(title);
            Integer cnt = anchorCount.getOrDefault(base, 0);
            String anchor = cnt == 0 ? base : base + "-" + cnt;
            anchorCount.put(base, cnt + 1);

            // 处理层级栈
            while (!stack.isEmpty() && stack.peek().getLevel() >= level) {
                stack.pop();
            }

            String parentId = stack.isEmpty() ? null : stack.peek().getId();

            ArticleChapter chapter = ArticleChapter.builder()
                    .id(UUIDUtils.signatureUuid(LocalDateTime.now(), articleId, order++))
                    .articleId(articleId)
                    .chapterOrder(order++)
                    .level(level)
                    .title(title)
                    .anchor(anchor)
                    .parentId(parentId)
                    .startOffset(start)
                    .endOffset(matcher.end())
                    .createdAt(LocalDateTime.now())
                    .updatedAt(LocalDateTime.now())
                    .build();

            chapters.add(chapter);
            stack.push(chapter);
        }

        // 批量保存章节并建立路径关系
        saveChaptersWithPaths(chapters);
    }

    /**
     * 保护代码块，将代码块替换为占位符，避免被误解析
     */
    private String protectCodeBlocks(String content) {
        if (content == null) {
            return "";
        }

        // 匹配代码块（三个反引号包裹的）
        Pattern codeBlockPattern = Pattern.compile("(?s)```[\\s\\S]*?```");
        Matcher codeBlockMatcher = codeBlockPattern.matcher(content);

        // 匹配内联代码（单个反引号包裹的）
        Pattern inlineCodePattern = Pattern.compile("`[^`]+`");

        // 先处理代码块
        StringBuffer result = new StringBuffer();
        List<String> codeBlocks = new ArrayList<>();

        while (codeBlockMatcher.find()) {
            String codeBlock = codeBlockMatcher.group(0);
            String placeholder = "###CODE_BLOCK_" + codeBlocks.size() + "###";
            codeBlocks.add(codeBlock);
            codeBlockMatcher.appendReplacement(result, placeholder);
        }
        codeBlockMatcher.appendTail(result);

        String processed = result.toString();

        // 再处理内联代码
        List<String> inlineCodes = new ArrayList<>();
        Matcher inlineMatcher = inlineCodePattern.matcher(processed);
        result = new StringBuffer();

        while (inlineMatcher.find()) {
            String inlineCode = inlineMatcher.group(0);
            String placeholder = "###INLINE_CODE_" + inlineCodes.size() + "###";
            inlineCodes.add(inlineCode);
            inlineMatcher.appendReplacement(result, placeholder);
        }
        inlineMatcher.appendTail(result);

        processed = result.toString();

        // 保存到临时存储，供恢复使用
        ThreadLocal<List<String>> codeBlocksStore = new ThreadLocal<>();
        ThreadLocal<List<String>> inlineCodesStore = new ThreadLocal<>();
        codeBlocksStore.set(codeBlocks);
        inlineCodesStore.set(inlineCodes);

        return processed;
    }

    /**
     * 恢复代码块
     */
    private String restoreCodeBlocks(String processedContent) {
        if (processedContent == null) {
            return "";
        }

        ThreadLocal<List<String>> inlineCodesStore = new ThreadLocal<>();
        ThreadLocal<List<String>> codeBlocksStore = new ThreadLocal<>();

        List<String> inlineCodes = inlineCodesStore.get();
        List<String> codeBlocks = codeBlocksStore.get();

        String result = processedContent;

        // 先恢复内联代码
        if (inlineCodes != null) {
            for (int i = 0; i < inlineCodes.size(); i++) {
                result = result.replace("###INLINE_CODE_" + i + "###", inlineCodes.get(i));
            }
        }

        // 再恢复代码块
        if (codeBlocks != null) {
            for (int i = 0; i < codeBlocks.size(); i++) {
                result = result.replace("###CODE_BLOCK_" + i + "###", codeBlocks.get(i));
            }
        }

        return result;
    }

    /**
     * 检查是否在代码块内
     */
    private boolean isInsideCodeBlock(String match, String processedContent, int start) {
        // 检查是否为代码块占位符
        if (match.contains("###CODE_BLOCK_") || match.contains("###INLINE_CODE_")) {
            return true;
        }

        // 检查前一行是否为空行（标题应该前面有空行或文档开头）
        if (start > 0) {
            // 查找前一个换行符
            int prevNewline = processedContent.lastIndexOf('\n', start - 1);
            if (prevNewline >= 0) {
                // 检查前一行内容
                String prevLine = processedContent.substring(prevNewline + 1, start).trim();
                // 如果前一行不是空行，且不是标题（以#开头），则可能是列表项或其他内容
                if (!prevLine.isEmpty() && !prevLine.startsWith("#")) {
                    // 检查是否是列表项（如1. xxx）
                    if (prevLine.matches("^\\d+\\.\\s+.+") ||
                            prevLine.matches("^[-*+]\\s+.+") ||
                            prevLine.matches("^>\\s+.+")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * 简化标题文本提取
     */
    private String extractPlainTextFromTitle(String titleWithHtml) {
        if (titleWithHtml == null || titleWithHtml.isEmpty()) {
            return "";
        }

        // 移除所有HTML标签
        String plainText = titleWithHtml.replaceAll("<[^>]*>", "");

        // 移除代码占位符
        plainText = plainText.replaceAll("###CODE_BLOCK_\\d+###", "");
        plainText = plainText.replaceAll("###INLINE_CODE_\\d+###", "");

        // 清理多余空格
        plainText = plainText.replaceAll("\\s+", " ").trim();

        if (plainText.isEmpty()) {
            // 尝试从属性中提取文本
            Pattern altPattern = Pattern.compile("alt\\s*=\\s*['\"]([^'\"]+)['\"]", Pattern.CASE_INSENSITIVE);
            Matcher altMatcher = altPattern.matcher(titleWithHtml);
            if (altMatcher.find()) {
                plainText = altMatcher.group(1);
            }
        }

        return plainText;
    }

    /**
     * 清理标题文本
     */
    private String cleanTitleText(String title) {
        if (title == null) {
            return "";
        }

        // 移除Markdown格式标记
        String cleaned = title
                .replaceAll("\\*\\*([^*]+)\\*\\*", "$1")   // 加粗
                .replaceAll("\\*([^*]+)\\*", "$1")         // 斜体
                .replaceAll("__([^_]+)__", "$1")          // 加粗（下划线）
                .replaceAll("_([^_]+)_", "$1")            // 斜体（下划线）
                .replaceAll("~~([^~]+)~~", "$1")          // 删除线
                .replaceAll("`([^`]+)`", "$1")            // 内联代码
                .replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1")  // 链接
                .replaceAll("!\\[[^\\]]+\\]\\([^)]+\\)", "");    // 图片

        // 清理空格
        cleaned = cleaned.replaceAll("\\s+", " ").trim();

        cleaned = cleaned.replaceAll("^[\\s,.;:!?]+|[\\s,.;:!?]+$", "");

        return cleaned;
    }

    /**
     * 批量保存章节并建立路径关系
     */
    private void saveChaptersWithPaths(List<ArticleChapter> chapters) {
        // 先保存所有章节
        for (ArticleChapter chapter : chapters) {
            chapterDao.save(chapter);
        }

        // 建立路径关系
        Map<String, ArticleChapter> chapterMap = new HashMap<>();
        for (ArticleChapter chapter : chapters) {
            chapterMap.put(chapter.getId(), chapter);
        }

        // 为每个章节计算路径
        for (ArticleChapter chapter : chapters) {
            StringBuilder path = new StringBuilder();

            if (chapter.getParentId() != null) {
                ArticleChapter parent = chapterMap.get(chapter.getParentId());
                if (parent != null && parent.getPath() != null) {
                    path.append(parent.getPath()).append("/");
                }
            }

            path.append(chapter.getId());
            chapter.setPath(path.toString());
            chapterDao.updateById(chapter);
        }
    }

    /**
     * slugify函数
     */
    private String slugify(String s) {
        if (s == null || s.isEmpty()) {
            return "section";
        }

        String t = s.toLowerCase();
        t = t.replaceAll("[^\\p{L}\\p{N}\\s-]", ""); // 只保留字母、数字、空格、连字符
        t = t.replaceAll("\\s+", "-");
        t = t.replaceAll("-+", "-");
        t = t.replaceAll("^-|-$", "");

        if (t.isEmpty()) {
            return "section";
        }

        if (t.length() > 1000) {
            t = t.substring(0, 1000);
        }

        return t;
    }
}

