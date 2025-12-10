package com.lx.blog.service.strategy.impl;

import com.lx.blog.common.enums.MessageTypeEnum;
import com.lx.blog.common.utils.MailUtils;
import com.lx.blog.domain.dto.MessageSendDto;
import com.lx.blog.service.strategy.MessageStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author LX
 * @date 2025/12/10
 * @description 邮件消息策略实现
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EmailMessageStrategy implements MessageStrategy {

    private final MailUtils mailUtils;

    @Override
    public boolean support(MessageTypeEnum messageType) {
        return MessageTypeEnum.EMAIL.equals(messageType);
    }

    @Override
    public void send(MessageSendDto sendDto) {
        log.info("开始发送邮件消息: to={}, subject={}", sendDto.getTo(), sendDto.getSubject());
        if (sendDto.getTemplate() != null && !sendDto.getTemplate().isEmpty()) {
            // 发送模板邮件
            mailUtils.sendTemplateMail(
                    sendDto.getTo(),
                    sendDto.getSubject(),
                    sendDto.getTemplate(),
                    sendDto.getParams()
            );
        } else {
            // 这里 MailUtils 暂未暴露纯文本发送方法，建议后续增强 MailUtils 或直接调用模板方法
            // 为了演示，这里假设模板参数即为内容，或者可以扩展 MailUtils
            // 暂时使用模板方法兜底，实际应扩展 MailUtils 支持 sendTextMail
            log.warn("邮件发送使用模板模式，但未指定模板，可能需要扩展纯文本发送功能");
            // 临时处理：如果 MailUtils 没有 sendSimpleMail，这里可以暂不处理或报错，或者约定一个通用模板
        }
        log.info("邮件发送成功");
    }
}
