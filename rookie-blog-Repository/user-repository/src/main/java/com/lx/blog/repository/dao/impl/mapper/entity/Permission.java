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
 * @date 2025/11/14
 * @description 权限实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("permission")
public class Permission {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 权限唯一编码（业务唯一键）
     */
    @TableField("code")
    private String code;

    /**
     * 权限显示名称
     */
    @TableField("name")
    private String name;

    /**
     * 权限描述
     */
    @TableField("description")
    private String description;

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