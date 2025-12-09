package com.lx.blog.service.auth.biz.impl;

import cn.dev33.satoken.temp.SaTempUtil;
import com.lx.blog.common.exception.NotFoundException;
import com.lx.blog.common.base.Result;
import com.lx.blog.common.exception.ValidationException;
import com.lx.blog.repository.dao.UserDao;
import com.lx.blog.repository.dao.impl.mapper.entity.User;
import com.lx.blog.service.auth.biz.UserEmailBizService;
import com.lx.blog.service.biz.UserBaseBizService;
import com.lx.blog.service.email.EmailService;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

/**
 * @author LX
 * @date 2025/12/3
 * @description 邮箱业务服务实现类
 */
@Service
public class UserUserEmailBizServiceImplUser extends UserBaseBizService implements UserEmailBizService {

    @NotNull private final EmailService emailService;

    public UserUserEmailBizServiceImplUser(UserDao userDao, EmailService emailService) {
        super(userDao);
        this.emailService = emailService;
    }

    /**
     * 是否已完成邮箱验证
     * @return 是否已验证
     */
    @Override
    public Result<Boolean> isEmailVerified() {
        String userId = getUserId();
        User user = userDao.getById(userId);
        return Result.ok(user != null && Integer.valueOf(1).equals(user.getEmailVerified()));
    }

    /**
     * 申请邮箱验证（发送验证邮件）
     * @return 申请结果
     */
    @Override
    public Result<Object> requestEmailVerification() {
        User user = userDao.getById(getUserId());
        if (user == null) {
            throw new NotFoundException(I18n("user.notfound"));
        }
        if (Integer.valueOf(1).equals(user.getEmailVerified())) {
            return Result.ok();
        }
        String token = SaTempUtil.createToken(getUserId(), 24 * 60 * 60);
        String verifyUrl = appHost + "/api/user/email/verification/confirm?token=" + token;
        emailService.sendVerificationEmail(user.getId(), user.getEmail(), verifyUrl);
        return Result.ok();
    }

    /**
     * 确认邮箱验证
     * @param token 临时验证令牌
     * @return 验证是否成功
     */
    @Override
    public Result<Object> confirmEmailToken(String token) {
        Object val = SaTempUtil.parseToken(token);
        if (val == null) {
            throw new ValidationException(I18n("token.invalid"));
        }
        String userId = String.valueOf(val);
        User user = userDao.getById(userId);
        if (user == null) {
            throw new NotFoundException(I18n("user.notfound"));
        }
        if (Integer.valueOf(1).equals(user.getEmailVerified())) {
            return Result.ok();
        }
        user.setEmailVerified(1);
        user.setEmailVerifiedAt(java.time.LocalDateTime.now());
        userDao.updateById(user);
        // 令牌一次性使用：清除令牌
        SaTempUtil.deleteToken(token);
        return Result.ok(I18n("email.verify.success"));
    }
}
