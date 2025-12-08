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
 * @description 用户设置实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_setting")
public class UserSetting {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 用户ID（UUID）
     */
    @TableField("user_id")
    private String userId;

    /**
     * 主题：light/dark
     */
    @TableField("theme")
    private String theme;

    /**
     * 是否接收站内通知
     */
    @TableField("notify_enabled")
    private Boolean notifyEnabled;

    /**
     * 语言偏好，如 zh-CN/en-US
     */
    @TableField("language")
    private String language;

    /**
     * 时区，如 Asia/Shanghai
     */
    @TableField("timezone")
    private String timezone;

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
