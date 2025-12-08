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
 * @description 分类实体
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("category")
public class Category {

    /**
     * 分类ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 名称
     */
    @TableField("name")
    private String name;

    /**
     * 唯一编码
     */
    @TableField("code")
    private String code;

    /**
     * 父分类ID
     */
    @TableField("parent_id")
    private String parentId;

    /**
     * 物化路径
     */
    @TableField("path")
    private String path;

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

