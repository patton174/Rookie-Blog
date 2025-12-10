package com.lx.blog.service.email;

/**
 * @author LX
 * @date 2025/11/14
 * @description 邮件服务接口
 */
public interface EmailService {

    /**
     * 发送邮箱验证邮件
     * @param email 收件邮箱
     * @param verifyUrl 验证链接
     */
    void sendVerificationEmail(String userId, String email, String verifyUrl);
}