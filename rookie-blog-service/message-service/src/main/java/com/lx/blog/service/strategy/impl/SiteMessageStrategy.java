package com.lx.blog.service.strategy.impl;

import com.lx.blog.common.enums.MessageTypeEnum;
import com.lx.blog.domain.dto.MessageSendDto;
import com.lx.blog.service.msg.ws.WebSocketServer;
import com.lx.blog.service.strategy.MessageStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author LX
 * @date 2025/12/10
 * @description 站内信消息策略实现
 */
@Slf4j
@Component
public class SiteMessageStrategy implements MessageStrategy {

    @Override
    public boolean support(MessageTypeEnum messageType) {
        return MessageTypeEnum.SITE_MESSAGE.equals(messageType);
    }

    @Override
    public void send(MessageSendDto sendDto) {
        log.info("开始发送站内信: to={}, content={}", sendDto.getTo(), sendDto.getContent());
        
        // 1. 推送 WebSocket 消息
        // 这里简单推送内容，实际可以推送 JSON 格式的消息对象
        WebSocketServer.sendInfo(sendDto.getContent(), sendDto.getTo());
        
        // TODO: 2. 将消息保存到数据库 user_message 表 (持久化)
        // userMessageDao.save(...)
        
        log.info("站内信发送完成");
    }
}
