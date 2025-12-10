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
 * @description 文章-分类关联实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("article_category")
public class ArticleCategory {

    /**
     * 关联ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 文章ID
     */
    @TableField("article_id")
    private String articleId;

    /**
     * 分类ID
     */
    @TableField("category_id")
    private String categoryId;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}

