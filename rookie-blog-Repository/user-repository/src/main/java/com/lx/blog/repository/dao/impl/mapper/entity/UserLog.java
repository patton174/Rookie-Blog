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
 * @description 登录日志实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("user_log")
public class UserLog {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 方法类名称
     */
    @TableField("_class")
    private String _class;

    /**
     * 操作行为
     */
    @TableField("action")
    private String action;

    /**
     * 用户ID（UUID），可为空
     */
    @TableField("user_id")
    private String userId;

    /**
     * 登录 IP
     */
    @TableField("ip")
    private String ip;

    /**
     * UA 标识
     */
    @TableField("user_agent")
    private String userAgent;

    /**
     * 登录时间
     */
    @TableField("logged_at")
    private LocalDateTime loggedAt;
}