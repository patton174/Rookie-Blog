package com.lx.blog.repository.listener;

import com.lx.blog.common.aop.log.OpLogEvent;
import com.lx.blog.common.response.Result;
import com.lx.blog.common.utils.UUIDUtils;
import com.lx.blog.repository.dao.ArticleStatsDao;
import com.lx.blog.repository.dao.ArticleDao;
import com.lx.blog.repository.dao.impl.mapper.entity.Article;
import com.lx.blog.repository.dao.ArticleLogDao;
import com.lx.blog.repository.dao.impl.mapper.entity.ArticleLog;
import com.lx.blog.common.utils.ServletUtils;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @author LX
 * @date 2025/12/03
 * @description 文章阅读日志事件监听器
 */
@Component
@RequiredArgsConstructor
public class ArticleOpLogEventListener {

    @NotNull private final ArticleLogDao logDao;
    @NotNull private final ArticleStatsDao statsDao;
    @NotNull private final ArticleDao articleDao;

    /**
     * 处理文章阅读日志事件
     *
     * @param event 文章阅读日志事件
     */
    @EventListener
    public void onOpLog(OpLogEvent event) {
        if (!"article_log".equals(event.getTableName())) return;
        Map<String, Object> p = event.getPayload();
        String userId = (String) p.get("userId");
        String ip = (String) p.get("ip");
        String ua = (String) p.get("user_agent");
        String referer = (String) p.get("referer");
        LocalDateTime at = (LocalDateTime) p.get("logged_at");

        @SuppressWarnings("unchecked")
        Map<String, String> params = (Map<String, String>) p.get("params");
        @SuppressWarnings("unchecked")
        Map<String, String[]> paramsArray = (Map<String, String[]>) p.get("params_array");

        String articleId = null;
        String slug = null;
        if (params != null) {
            articleId = params.get("articleId");
            if (articleId == null) articleId = params.get("id");
            if (articleId == null) articleId = params.get("article_id");
            slug = params.get("slug");
        }
        if ((articleId == null || articleId.isEmpty()) && paramsArray != null) {
            String[] arr = paramsArray.get("articleId");
            if (arr == null) arr = paramsArray.get("id");
            if (arr == null) arr = paramsArray.get("article_id");
            if (arr != null && arr.length > 0) articleId = arr[0];

            String[] sArr = paramsArray.get("slug");
            if ((slug == null || slug.isEmpty()) && sArr != null && sArr.length > 0) slug = sArr[0];
        }

        if (articleId == null || articleId.isEmpty()) {
            Object ret = p.get("ret");
            if (ret instanceof Result) {
                Object data = ((Result<?>) ret).getData();
                if (data != null) {
                    if (data instanceof Map) {
                        Map<?, ?> map = (Map<?, ?>) data;
                        Object id = map.get("id");
                        if (id == null) id = map.get("articleId");
                        if (id != null) articleId = id.toString();
                    } else {
                        try {
                            java.lang.reflect.Method getId = data.getClass().getMethod("getId");
                            Object id = getId.invoke(data);
                            if (id != null) articleId = id.toString();
                        } catch (Exception ignored) {
                            try {
                                java.lang.reflect.Method getArticleId = data.getClass().getMethod("getArticleId");
                                Object id = getArticleId.invoke(data);
                                if (id != null) articleId = id.toString();
                            } catch (Exception ignored2) {}
                        }
                    }
                }
            }
        }

        if ((slug == null || slug.isEmpty())) {
            try {
                String uri = ServletUtils.getRequest().getRequestURI();
                if (uri != null) {
                    int idx = uri.lastIndexOf('/');
                    if (idx >= 0 && idx + 1 < uri.length()) {
                        slug = uri.substring(idx + 1);
                    }
                }
            } catch (Exception ignored) {}
        }
        if ((articleId == null || articleId.isEmpty()) && slug != null && !slug.isEmpty()) {
            Article art = articleDao.getBySlug(slug);
            if (art != null) articleId = art.getId();
        }
        if (articleId == null || articleId.isEmpty()) return;
        String uid = (userId == null || userId.isEmpty()) ? "visitor" : userId;
        String _class = (String) p.get("_class");
        ArticleLog v = ArticleLog.builder()
                .id(UUIDUtils.signatureUuid(LocalDateTime.now(), _class))
                ._class(_class)
                .action((String) p.get("action"))
                .userId(uid)
                .articleId(articleId)
                .ip(ip)
                .ua(ua)
                .viewAt(at)
                .referer(referer)
                .build();
        logDao.appendView(v);
        statsDao.incViews(articleId, 1);
    }
}
