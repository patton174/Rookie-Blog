package com.lx.blog.service.msg.impl;

import com.lx.blog.domain.dto.MessageSendDto;
import com.lx.blog.service.msg.MessageService;
import com.lx.blog.service.strategy.MessageStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author LX
 * @date 2025/12/10
 * @description 统一消息服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final List<MessageStrategy> messageStrategies;

    @Async("taskExecutor")
    @Override
    public void sendMessage(MessageSendDto sendDto) {
        if (sendDto == null || sendDto.getMessageType() == null) {
            log.warn("消息发送失败: 参数为空或未指定消息类型");
            return;
        }

        boolean processed = false;
        for (MessageStrategy strategy : messageStrategies) {
            if (strategy.support(sendDto.getMessageType())) {
                try {
                    strategy.send(sendDto);
                    processed = true;
                } catch (Exception e) {
                    log.error("消息发送异常: type={}, to={}, error={}", 
                            sendDto.getMessageType(), sendDto.getTo(), e.getMessage(), e);
                }
                // 找到对应策略并执行后即可退出循环（假设一种类型对应一个策略）
                break; 
            }
        }

        if (!processed) {
            log.warn("未找到对应的消息发送策略: type={}", sendDto.getMessageType());
        }
    }
}
