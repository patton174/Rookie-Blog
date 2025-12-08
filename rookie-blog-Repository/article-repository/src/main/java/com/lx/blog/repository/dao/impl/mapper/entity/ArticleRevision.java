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
 * @description 文章修订/草稿实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("article_revision")
public class ArticleRevision {

    /**
     * 修订ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 文章ID
     */
    @TableField("article_id")
    private String articleId;

    /**
     * 编辑器版本
     */
    @TableField("editor_version")
    private String editorVersion;

    /**
     * 内容快照
     */
    @TableField("content_snapshot")
    private String contentSnapshot;

    /**
     * 差异JSON
     */
    @TableField("diff_json")
    private String diffJson;

    /**
     * 保存时间
     */
    @TableField("saved_at")
    private LocalDateTime savedAt;

    /**
     * 保存人
     */
    @TableField("saved_by")
    private String savedBy;
}

