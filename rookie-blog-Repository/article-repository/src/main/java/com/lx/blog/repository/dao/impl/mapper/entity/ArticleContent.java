package com.lx.blog.repository.dao.impl.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author LX
 * @date 2025/12/04
 * @description 文章内容实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("article_content")
public class ArticleContent {

    /**
     * 文章ID（主键）
     */
    @TableId(value = "article_id")
    private String articleId;

    /**
     * Markdown内容
     */
    @TableField("content_md")
    private String contentMd;

    /**
     * HTML内容
     */
    @TableField("content_html")
    private String contentHtml;

    /**
     * 字数
     */
    @TableField("words_count")
    private Integer wordsCount;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
