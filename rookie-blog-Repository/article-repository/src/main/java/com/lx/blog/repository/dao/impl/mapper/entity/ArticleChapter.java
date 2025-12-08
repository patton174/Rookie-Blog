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
 * @date 2025/12/03
 * @description 文章章节元数据实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("article_chapter")
public class ArticleChapter {

    /**
     * 章节ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 文章ID
     */
    @TableField("article_id")
    private String articleId;

    /**
     * 章节顺序；0为虚拟章节（前言）
     */
    @TableField("chapter_order")
    private Integer chapterOrder;

    /**
     * 标题层级：0~6
     */
    @TableField("level")
    private Integer level;

    /**
     * 章节标题
     */
    @TableField("title")
    private String title;

    /**
     * 章节锚点
     */
    @TableField("anchor")
    private String anchor;

    /**
     * 父章节ID
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 物化路径
     */
    @TableField("path")
    private String path;

    /**
     * 原Markdown开始位置
     */
    @TableField("start_offset")
    private Integer startOffset;

    /**
     * 原Markdown结束位置
     */
    @TableField("end_offset")
    private Integer endOffset;

    /**
     * 章节内容哈希
     */
    @TableField("hash")
    private String hash;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}

