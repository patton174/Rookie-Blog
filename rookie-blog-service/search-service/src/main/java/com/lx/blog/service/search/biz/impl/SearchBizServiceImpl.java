package com.lx.blog.service.search.biz.impl;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.QueryBuilders;
import co.elastic.clients.elasticsearch.core.DeleteRequest;
import co.elastic.clients.elasticsearch.core.IndexRequest;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import com.lx.blog.common.base.Result;
import com.lx.blog.service.search.biz.SearchBizService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author LX
 * @date 2025/12/03
 * @description 搜索业务服务实现（Elasticsearch）
 */
@Service
@RequiredArgsConstructor
public class SearchBizServiceImpl implements SearchBizService {

    private static final String INDEX = "articles";

    @NotNull private final ElasticsearchClient client;

    /**
     * 索引文章
     *
     * @param id 文章ID
     * @param title 标题
     * @param summary 摘要
     * @param authorId 作者ID
     * @param publishAt 发布时间
     * @param tags 标签
     * @return 结果
     */
    @Override
    public Result<Object> indexArticle(String id, String title, String summary, String authorId, LocalDateTime publishAt, List<String> tags) {
        try {
            Map<String, Object> doc = new HashMap<>();
            doc.put("id", id);
            doc.put("title", title);
            doc.put("summary", summary);
            doc.put("authorId", authorId);
            doc.put("publishAt", publishAt != null ? publishAt.toString() : null);
            doc.put("tags", tags);
            client.index(IndexRequest.of(b -> b.index(INDEX).id(id).document(doc)));
            return Result.ok();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 关键词搜索文章
     *
     * @param q 关键词
     * @param page 页码（从1开始）
     * @param size 每页数量
     * @return 命中列表
     */
    @Override
    public Result<List<Map<String, Object>>> search(String q, int page, int size) {
        try {
            int from = Math.max((page - 1) * size, 0);
            SearchRequest sr = SearchRequest.of(b -> b
                    .index(INDEX)
                    .from(from)
                    .size(size)
                    .query(QueryBuilders.multiMatch(m -> m
                            .query(q)
                            .fields("title", "summary", "tags")
                    ))
            );
            SearchResponse<Map> resp = client.search(sr, Map.class);
            List<Map<String, Object>> list = resp.hits().hits().stream()
                    .map(h -> (Map<String, Object>) h.source())
                    .collect(Collectors.toList());
            return Result.ok(list);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 删除文章索引
     *
     * @param id 文章ID
     * @return 结果
     */
    @Override
    public Result<Object> delete(String id) {
        try {
            client.delete(DeleteRequest.of(b -> b.index(INDEX).id(id)));
            return Result.ok();
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

