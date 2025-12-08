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
 * @description 文章附件实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("article_attachment")
public class ArticleAttachment {

    /**
     * 附件ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 文章ID
     */
    @TableField("article_id")
    private String articleId;

    /**
     * 文件URL
     */
    @TableField("file_url")
    private String fileUrl;

    /**
     * 文件类型
     */
    @TableField("file_type")
    private String fileType;

    /**
     * 文件大小（字节）
     */
    @TableField("size")
    private Long size;

    /**
     * 文件哈希
     */
    @TableField("hash")
    private String hash;

    /**
     * 上传时间
     */
    @TableField("uploaded_at")
    private LocalDateTime uploadedAt;
}

