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
 * @description 用户权限实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_permission")
public class UserPermission {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 用户ID（UUID）
     */
    @TableField("user_id")
    private String userId;

    /**
     * 权限ID
     */
    @TableField("permission_id")
    private String permissionId;

    /**
     * 生效方式：grant 授权，deny 拒绝
     */
    @TableField("effect")
    private String effect;

    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
