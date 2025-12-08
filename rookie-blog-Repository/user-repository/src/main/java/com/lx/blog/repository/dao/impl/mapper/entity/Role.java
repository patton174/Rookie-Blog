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
 * @description 角色实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("role")
public class Role {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 角色唯一编码（业务唯一键）
     */
    @TableField("code")
    private String code;

    /**
     * 角色显示名称
     */
    @TableField("name")
    private String name;

    /**
     * 角色描述
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