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
 * @description 文章-标签关联实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("article_tag")
public class ArticleTag {

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
     * 标签ID
     */
    @TableField("tag_id")
    private String tagId;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}

