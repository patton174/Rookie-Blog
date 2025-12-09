package com.lx.blog.service.auth.biz;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.lx.blog.common.base.Result;
import com.lx.blog.domain.dto.LoginDto;
import com.lx.blog.domain.dto.RegisterDto;
import com.lx.blog.domain.dto.UpdatePasswordDto;

/**
 * @author LX
 * @date 2025/11/14
 * @description 用户认证业务服务接口
 */
public interface UserAuthBizService {

    /**
     * 用户登录
     *
     * @param req 登录请求参数
     * @return 登录结果
     */
    Result<SaTokenInfo> login(LoginDto req);

    /**
     * 用户注册
     *
     * @param req 注册请求参数
     * @return 注册结果
     */
    Result<Object> register(RegisterDto req);

    /**
     * 用户注销
     *
     * @return 注销结果
     */
    Result<Object> logout();

    /**
     * 更新用户密码
     *
     * @param req 更新密码请求参数
     * @return 更新结果
     */
    Result<Object> updatePassword(UpdatePasswordDto req);

}
