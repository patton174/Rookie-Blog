package com.lx.blog.service.msg;

import com.lx.blog.domain.dto.MessageSendDto;

/**
 * @author LX
 * @date 2025/12/10
 * @description 统一消息服务接口
 */
public interface MessageService {

    /**
     * 发送消息（异步）
     * @param sendDto 消息数据
     */
    void sendMessage(MessageSendDto sendDto);
}
