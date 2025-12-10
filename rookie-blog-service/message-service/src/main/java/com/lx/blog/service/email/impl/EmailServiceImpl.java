package com.lx.blog.service.email.impl;

import com.lx.blog.common.enums.MessageTypeEnum;
import com.lx.blog.domain.dto.MessageSendDto;
import com.lx.blog.service.email.EmailService;
import com.lx.blog.service.msg.MessageService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @author LX
 * @date 2025/11/14
 * @description 邮件服务实现
 */
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    @NotNull private final MessageService messageService;
    @Value("${app.host:http://localhost:8080}")
    private String appHost;

    /**
     * 发送邮箱验证邮件
     * @param email 收件邮箱
     * @param verifyUrl 验证链接
     */
    @Override
    public void sendVerificationEmail(String userId, String email, String verifyUrl) {
        Map<String, Object> model = new HashMap<>();
        model.put("username", userId);
        model.put("verifyUrl", verifyUrl);
        model.put("expireHours", 24);

        MessageSendDto sendDto = MessageSendDto.builder()
                .messageType(MessageTypeEnum.EMAIL)
                .to(email)
                .subject("邮箱验证")
                .template("templates/mail/verify-email.html")
                .params(model)
                .bizId(userId)
                .bizType("verify_code")
                .build();

        messageService.sendMessage(sendDto);
    }
}