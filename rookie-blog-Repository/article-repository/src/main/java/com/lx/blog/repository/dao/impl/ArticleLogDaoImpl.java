package com.lx.blog.repository.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.blog.repository.dao.ArticleLogDao;
import com.lx.blog.repository.dao.impl.mapper.ArticleLogMapper;
import com.lx.blog.repository.dao.impl.mapper.entity.ArticleLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LX
 * @date 2025/12/03
 * @description 文章阅读明细数据访问实现
 */
@Slf4j
@Repository
@RequiredArgsConstructor
public class ArticleLogDaoImpl extends ServiceImpl<ArticleLogMapper, ArticleLog> implements ArticleLogDao {

    /**
     * 记录文章相关日志（阅读、评论、点赞、回复）
     *
     * @param logs 文章相关日志实体
     */
    @Override
    public void appendView(ArticleLog logs) {
        if (logs == null) return;

        // 仅针对阅读记录进行更新（去重）
        if ("view".equals(logs.getAction())) {
            ArticleLog articleLog = baseMapper.selectOne(new LambdaQueryWrapper<ArticleLog>()
                    .eq(ArticleLog::getUserId, logs.getUserId())
                    .eq(ArticleLog::getArticleId, logs.getArticleId())
                    .eq(ArticleLog::getAction, "view")
                    .last("LIMIT 1"));
            
            if (articleLog != null) {
                // 更新现有阅读记录
                articleLog.setIp(logs.getIp());
                articleLog.setUa(logs.getUa());
                articleLog.setReferer(logs.getReferer());
                articleLog.setViewAt(logs.getViewAt());
                baseMapper.updateById(articleLog);
                return;
            }
        }
        
        // 其他类型日志（如发布、点赞等）
        baseMapper.insert(logs);
    }

    /**
     * 查询用户阅读历史文章列表
     *
     * @param userId 用户ID
     * @return 文章ID列表
     */
    @Override
    public List<String> listHistoryArticleIds(String userId) {
        return baseMapper.selectList(new LambdaQueryWrapper<ArticleLog>()
                .select(ArticleLog::getArticleId, ArticleLog::getViewAt) // 优化查询字段
                .eq(ArticleLog::getUserId, userId)
                .orderByDesc(ArticleLog::getViewAt))
                .stream()
                .map(ArticleLog::getArticleId)
                .distinct() // 内存去重
                .collect(Collectors.toList());
    }
}

