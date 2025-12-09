package com.lx.blog.service.auth.biz.impl;

import com.lx.blog.common.api.IpParseApi;
import com.lx.blog.common.exception.NotFoundException;
import com.lx.blog.common.base.Result;
import com.lx.blog.domain.vo.UserVo;
import com.lx.blog.repository.dao.UserDao;
import com.lx.blog.repository.dao.impl.mapper.entity.User;
import com.lx.blog.service.auth.biz.UserProfileBizService;
import com.lx.blog.service.biz.UserBaseBizService;
import jakarta.validation.constraints.NotNull;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Map;

/**
 * @author LX
 * @date 2025/12/3
 * @description 用户个人信息业务服务实现类
 */
@Service
public class UserProfileBizServiceImplUser extends UserBaseBizService implements UserProfileBizService {

    @NotNull private final IpParseApi ipParseApi;

    public UserProfileBizServiceImplUser(UserDao userDao, IpParseApi ipParseApi) {
        super(userDao);
        this.ipParseApi = ipParseApi;
    }

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户信息
     */
    @Override
    public Result<UserVo> getProfile() {
        User user = userDao.getById(getUserId());
        if (user == null) {
            throw new NotFoundException(I18n("user.notfound"));
        }
        UserVo userVo = UserVo.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .status(user.getStatus())
                .lastLogin(user.getLastLoginAt())
                .avatarUrl(user.getAvatarUrl())
                .emailVerified(Integer.valueOf(1).equals(user.getEmailVerified()))
                .build();
        try {
            String ipAddress = ipParseApi.parseIpAddress(user.getLastLoginIp()).getRegion();
            userVo.setIpAddress(ipAddress.isEmpty() ? I18n("common.unknown") : ipAddress);
        } catch (IOException e) {
            userVo.setIpAddress(I18n("common.unknown"));
        }
        return Result.ok(userVo);
    }

    /**
     * 根据用户ID获取用户名
     *
     * @param userId 用户ID
     * @return 用户名
     */
    @Override
    public Result<Map<String, String>> getUsernameById(String userId) {
        User user = userDao.getById(userId);
        if (user == null) {
            throw new NotFoundException(I18n("user.notfound"));
        }
        return Result.ok(Map.of("username", user.getUsername()));
    }
}
