package com.lx.blog.repository.dao.impl.mapper.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author LX
 * @date 2025/11/13
 * @description 用户实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user")
public class User {

    /**
     * 主键 uuid
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

     /**
     * 用户名
     */
    private String username;

     /**
     * 密码
     */
    private String password;

     /**
     * 邮箱
     */
    private String email;

     /**
     * 状态 0：禁用 1：启用
     */
    private Integer status;

    /**
     * 最后登录时间
     */
    private LocalDateTime lastLoginAt;

    /**
     * 最后登录IP
     */
    private String lastLoginIp;

    /**
     * 头像地址 URL
     */
    private String avatarUrl;

     /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 逻辑删除 0：未删除 1：已删除
     */
    @TableLogic
    private Integer idDeleted;

    /**
     * 邮箱是否已验证 0：否 1：是
     */
    private Integer emailVerified;

    /**
     * 邮箱验证时间
     */
    private LocalDateTime emailVerifiedAt;

}
