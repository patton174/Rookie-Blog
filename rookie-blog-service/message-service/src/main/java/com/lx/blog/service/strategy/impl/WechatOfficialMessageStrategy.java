package com.lx.blog.service.strategy.impl;

import com.lx.blog.common.enums.MessageTypeEnum;
import com.lx.blog.domain.dto.MessageSendDto;
import com.lx.blog.service.strategy.MessageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author LX
 * @date 2025/12/10
 * @description 微信公众号消息策略实现
 */
@Slf4j
@Component
public class WechatOfficialMessageStrategy implements MessageStrategy {

    @Override
    public boolean support(MessageTypeEnum messageType) {
        return MessageTypeEnum.WECHAT_OFFICIAL.equals(messageType);
    }

    @Override
    public void send(MessageSendDto sendDto) {
        log.info("开始发送微信公众号消息: to={}, templateId={}", sendDto.getTo(), sendDto.getTemplate());
        // TODO: 接入 WxJava SDK 调用微信接口
        // 1. 获取 AccessToken
        // 2. 组装 WxMpTemplateMessage
        // 3. wxMpService.getTemplateMsgService().sendTemplateMsg(msg);
        log.info("微信公众号消息发送模拟成功 (待集成 WxJava)");
    }
}
