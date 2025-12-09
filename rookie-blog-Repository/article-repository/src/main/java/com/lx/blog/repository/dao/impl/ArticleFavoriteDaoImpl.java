package com.lx.blog.repository.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.blog.repository.dao.ArticleFavoriteDao;
import com.lx.blog.repository.dao.impl.mapper.ArticleFavoriteMapper;
import com.lx.blog.repository.dao.impl.mapper.entity.ArticleFavorite;
import com.lx.blog.common.utils.UUIDUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

/**
 * @author LX
 * @date 2025/12/03
 * @description 文章收藏数据访问实现
 */
@Repository
@RequiredArgsConstructor
public class ArticleFavoriteDaoImpl extends ServiceImpl<ArticleFavoriteMapper, ArticleFavorite> implements ArticleFavoriteDao {

    /**
     * 检查用户是否收藏了文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     * @return 如果用户收藏了文章则返回true，否则返回false
     */
    @Override
    public boolean hasFavorited(String articleId, String userId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<ArticleFavorite>()
                .eq(ArticleFavorite::getArticleId, articleId)
                .eq(ArticleFavorite::getUserId, userId)) > 0;
    }

    /**
     * 收藏文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     */
    @Override
    public void favorite(String articleId, String userId) {
        if (!hasFavorited(articleId, userId)) {
            ArticleFavorite fav = ArticleFavorite.builder()
                    .id(UUIDUtils.getId())
                    .articleId(articleId)
                    .userId(userId)
                    .build();
            baseMapper.insert(fav);
        }
    }

    /**
     * 取消收藏文章
     *
     * @param articleId 文章ID
     * @param userId    用户ID
     */
    @Override
    public void unfavorite(String articleId, String userId) {
        baseMapper.delete(new LambdaQueryWrapper<ArticleFavorite>()
                .eq(ArticleFavorite::getArticleId, articleId)
                .eq(ArticleFavorite::getUserId, userId));
    }

    /**
     * 统计收藏数量
     *
     * @param articleId 文章ID
     * @return 收藏数量
     */
    @Override
    public long favoriteCount(String articleId) {
        return baseMapper.selectCount(new LambdaQueryWrapper<ArticleFavorite>()
                .eq(ArticleFavorite::getArticleId, articleId));
    }
}

