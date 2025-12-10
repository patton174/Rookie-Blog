package com.lx.blog.repository.dao.impl.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author LX
 * @date 2025/12/03
 * @description 文章系列项实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("article_series_item")
public class ArticleSeriesItem {

    /**
     * 关联ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 系列ID
     */
    @TableField("series_id")
    private String seriesId;

    /**
     * 文章ID
     */
    @TableField("article_id")
    private String articleId;

    /**
     * 系列内排序
     */
    @TableField("sort_order")
    private Integer sortOrder;
}

