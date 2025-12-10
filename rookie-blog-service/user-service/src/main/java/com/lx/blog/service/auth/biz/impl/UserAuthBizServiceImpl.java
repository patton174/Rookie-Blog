package com.lx.blog.service.auth.biz.impl;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.lx.blog.common.base.Result;
import com.lx.blog.common.exception.ValidationException;
import com.lx.blog.domain.dto.UpdatePasswordDto;
import com.lx.blog.repository.dao.UserDao;
import com.lx.blog.repository.dao.impl.mapper.entity.User;
import com.lx.blog.service.auth.biz.UserAuthBizService;
import com.lx.blog.common.exception.ForbiddenException;
import com.lx.blog.common.exception.NotFoundException;
import com.lx.blog.common.utils.BCryptUtils;
import com.lx.blog.domain.dto.LoginDto;
import com.lx.blog.domain.dto.RegisterDto;
import com.lx.blog.service.biz.UserBaseBizService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * @author LX
 * @date 2025/11/14
 * @description 用户认证业务服务实现类
 */
@Service
public class UserAuthBizServiceImpl extends UserBaseBizService implements UserAuthBizService {

    public UserAuthBizServiceImpl(UserDao userDao) {
        super(userDao);
    }

    /**
     * 用户登录
     *
     * @param req 登录请求参数
     * @return 登录结果
     */
    @Override
    public Result<SaTokenInfo> login(LoginDto req) {
        User user = getUser(req.getUsername());
        if (user == null || !BCryptUtils.matches(req.getPassword(), user.getPassword())) {
            throw new ValidationException(I18n("user.login.invalid"));
        }
        if (user.getStatus() == 0) {
            throw new ForbiddenException(I18n("user.disabled"));
        }
        if (user.getLastLoginIp() != null && !user.getLastLoginIp().equals(req.getIpAddress())) {
            user.setLastLoginIp(req.getIpAddress());
        }
        user.setLastLoginAt(LocalDateTime.now());
        userDao.updateById(user);
        StpUtil.login(user.getId());
        return Result.ok(StpUtil.getTokenInfo());
    }

    /**
     * 用户注册
     *
     * @param req 注册请求参数
     * @return 注册结果
     */
    @Override
    public Result<Object> register(RegisterDto req) {
        User user = User.builder()
                .id(getId())
                .username(req.getUsername())
                .password(BCryptUtils.encode(req.getPassword()))
                .email(req.getEmail())
                .lastLoginIp(req.getIpAddress())
                .status(1)
                .build();
        userDao.save(user);
        return Result.ok();
    }

    /**
     * 用户注销
     *
     * @return 注销结果
     */
    @Override
    public Result<Object> logout() {
        StpUtil.logout();
        return Result.ok();
    }

    /**
     * 更新用户密码
     * @param req 更新密码请求参数
     * @return 更新结果
     */
    @Override
    public Result<Object> updatePassword(UpdatePasswordDto req) {
        User user = userDao.getById(getUserId());
        if (user == null) {
            throw new NotFoundException(I18n("user.notfound"));
        }
        if (!BCryptUtils.matches(req.getOldPassword(), user.getPassword())) {
            throw new ValidationException(I18n("password.old.invalid"));
        }
        if (!req.getNewPassword().equals(req.getConfirmPassword())) {
            throw new ValidationException(I18n("password.mismatch"));
        }
        user.setPassword(BCryptUtils.encode(req.getNewPassword()));
        userDao.updateById(user);
        return Result.ok();
    }
}
