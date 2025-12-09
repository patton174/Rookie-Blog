package com.lx.blog.service.auth.biz;

import com.lx.blog.common.base.Result;

/**
 * @author LX
 * @date 2025/12/3
 * @description 邮箱业务服务接口
 */
public interface UserEmailBizService {

    /**
     * 是否已完成邮箱验证
     *
     * @return 是否已验证
     */
    Result<Boolean> isEmailVerified();

    /**
     * 申请邮箱验证（发送验证邮件）
     *
     * @return 申请结果
     */
    Result<Object> requestEmailVerification();

    /**
     * 确认邮箱验证
     *
     * @param token 临时验证令牌
     * @return 验证是否成功
     */
    Result<Object> confirmEmailToken(String token);
}
