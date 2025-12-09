package com.lx.blog.service.auth.biz;

import com.lx.blog.common.base.Result;
import com.lx.blog.domain.dto.ArticleViewDto;

/**
 * @author LX
 * @date 2025/12/03
 * @description 文章互动业务服务接口
 */
public interface ArticleInteractionBizService {

    /**
     * 点赞文章
     *
     * @param articleId 文章ID
     * @return 结果
     */
    Result<Object> like(String articleId);

    /**
     * 取消点赞文章
     *
     * @param articleId 文章ID
     * @return 结果
     */
    Result<Object> unlike(String articleId);

    /**
     * 收藏文章
     *
     * @param articleId 文章ID
     * @return 结果
     */
    Result<Object> favorite(String articleId);

    /**
     * 取消收藏文章
     *
     * @param articleId 文章ID
     * @return 结果
     */
    Result<Object> unfavorite(String articleId);

     /**
      * 检查用户是否点赞文章
      *
      * @param articleId 文章ID
      * @return 是否点赞
      */
    Result<Boolean> isLiked(String articleId);

    /**
     * 检查用户是否收藏文章
     *
     * @param articleId 文章ID
     * @return 是否收藏
     */
    Result<Boolean> isFavorited(String articleId);

    /**
     * 统计文章点赞数量
     *
     * @param articleId 文章ID
     * @return 点赞数量
     */
    Result<Long> likeCount(String articleId);

    /**
     * 统计文章收藏数量
     *
     * @param articleId 文章ID
     * @return 收藏数量
     */
    Result<Long> favoriteCount(String articleId);
}

