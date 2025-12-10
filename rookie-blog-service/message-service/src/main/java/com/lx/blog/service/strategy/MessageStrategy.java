package com.lx.blog.service.strategy;

import com.lx.blog.common.enums.MessageTypeEnum;
import com.lx.blog.domain.dto.MessageSendDto;

/**
 * @author LX
 * @date 2025/12/10
 * @description 消息发送策略接口
 */
public interface MessageStrategy {

    /**
     * 是否支持该消息类型
     * @param messageType 消息类型
     * @return boolean
     */
    boolean support(MessageTypeEnum messageType);

    /**
     * 发送消息
     * @param sendDto 消息数据
     */
    void send(MessageSendDto sendDto);
}
