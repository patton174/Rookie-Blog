package com.lx.blog.service.search.biz;

import com.lx.blog.common.base.Result;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author LX
 * @date 2025/12/03
 * @description 搜索业务服务接口（Elasticsearch）
 */
public interface SearchBizService {

    /**
     * 索引文章
     * @param id 文章ID
     * @param title 标题
     * @param summary 摘要
     * @param authorId 作者ID
     * @param publishAt 发布时间
     * @param tags 标签
     * @return 结果
     */
    Result<Object> indexArticle(String id, String title, String summary, String authorId, LocalDateTime publishAt, List<String> tags);

    /**
     * 关键词搜索文章
     * @param q 关键词
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 命中列表
     */
    Result<List<Map<String, Object>>> search(String q, int page, int size);

    /**
     * 删除文章索引
     * @param id 文章ID
     * @return 结果
     */
    Result<Object> delete(String id);
}

