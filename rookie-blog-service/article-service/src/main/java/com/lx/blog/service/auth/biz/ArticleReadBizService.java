package com.lx.blog.service.auth.biz;

import com.lx.blog.common.base.Result;
import com.lx.blog.domain.vo.ArticleChapterVo;
import com.lx.blog.domain.vo.ArticleContentVo;
import com.lx.blog.domain.vo.ArticleVo;
import com.lx.blog.domain.vo.TagStatVo;

import java.util.List;

/**
 * @author LX
 * @date 2025/12/03
 * @description 文章阅读查询业务服务接口
 */
public interface ArticleReadBizService {

    /**
     * 根据slug查询文章
     *
     * @param slug SEO别名
     * @return 文章
     */
    Result<ArticleVo> getBySlug(String slug);
    
    /**
     * 查询文章内容
     *
     * @param articleId 文章ID
     * @return 文章内容
     */
    Result<ArticleContentVo> getContent(String articleId);

    /**
     * 查询文章章节目录
     *
     * @param articleId 文章ID
     * @return 章节列表
     */
    Result<List<ArticleChapterVo>> listChapters(String articleId);

    /**
     * 查询已发布文章列表
     *
     * @return 文章列表
     */
    Result<List<ArticleVo>> listPublished();

    /**
     * 查询用户浏览历史
     *
     * @return 文章列表
     */
    Result<List<ArticleVo>> listHistory();

    /**
     * 查询热门标签
     *
     * @return 标签列表
     */
    Result<List<TagStatVo>> listPopularTags();

    /**
     * 根据文章Id查询文章基础信息
     *
     * @param articleId 文章ID
     * @return 文章信息
     */
    Result<ArticleVo> getById(String articleId);
}

