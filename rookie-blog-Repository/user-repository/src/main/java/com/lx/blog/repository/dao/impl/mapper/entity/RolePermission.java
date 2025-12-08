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
 * @description 角色权限实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("role_permission")
public class RolePermission {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /** 角色ID */
    @TableField("role_id")
    private String roleId;

    /** 权限ID */
    @TableField("permission_id")
    private String permissionId;

    /** 创建时间 */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
