package com.lx.blog.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LX
 * @date 2025/12/10
 * @description 消息类型枚举
 */
@Getter
@AllArgsConstructor
public enum MessageTypeEnum {

    /**
     * 邮件
     */
    EMAIL("email", "邮件消息"),

    /**
     * 微信公众号
     */
    WECHAT_OFFICIAL("wechat_official", "微信公众号消息"),

    /**
     * 站内信
     */
    SITE_MESSAGE("site_message", "站内信消息"),

    /**
     * 短信（预留）
     */
    SMS("sms", "短信消息");

    private final String code;
    private final String desc;
}
