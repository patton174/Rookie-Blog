package com.lx.blog.service.auth.biz;

import com.lx.blog.common.base.Result;
import com.lx.blog.domain.vo.UserVo;

import java.util.Map;

/**
 * @author LX
 * @date 2025/12/3
 * @description 用户个人信息业务服务接口
 */
public interface UserProfileBizService {

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户信息
     */
    Result<UserVo> getProfile();

    /**
     * 根据用户ID获取用户名
     *
     * @param userId 用户ID
     * @return 用户名
     */
    Result<Map<String, String>> getUsernameById(String userId);
}
