package com.lx.blog.web.controller.user.auth;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.lx.blog.domain.dto.UpdatePasswordDto;
import com.lx.blog.common.response.Result;
import com.lx.blog.domain.dto.LoginDto;
import com.lx.blog.domain.dto.RegisterDto;
import com.lx.blog.service.auth.biz.UserAuthBizService;
import com.lx.blog.common.aop.log.OpLog;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户控制器
 *
 * @author 李旭
 * @date 2025/11/14
 * @description 用户控制器
 */
@Tag(name = "用户控制器", description = "用户登录、注册、信息查询、注销等操作")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user/auth")
public class UserAuthController {

    @NotNull private final UserAuthBizService authBiz;

    /**
     * 用户登录
     *
     * @param req 登录请求参数
     * @return 登录结果
     */
    @PostMapping("/login")
    @Operation(summary = "用户登录", description = "用户通过用户名和密码登录系统")
    @OpLog(action = "login", func = "user.auth.login")
    public Result<SaTokenInfo> doLogin(@RequestBody LoginDto req) {
        return authBiz.login(req);
    }

     /**
      * 用户注册
      *
      * @param req 注册请求参数
      * @return 注册结果
      */
    @PostMapping("/register")
    @Operation(summary = "用户注册", description = "用户通过用户名、密码、邮箱注册系统")
    public Result<Object> doRegister(@RequestBody RegisterDto req) {
        return authBiz.register(req);
    }

    /**
     * 用户注销登录
     *
     * @return 注销结果
     */
    @PostMapping("/logout")
    @Operation(summary = "用户注销登录", description = "用户注销登录系统")
    public Result<Object> logout() {
        return authBiz.logout();
    }

    /**
     * 更新用户密码
     *
     * @param req 更新密码请求参数
     * @return 更新结果
     */
    @PostMapping("/password/update")
    @Operation(summary = "更新用户密码", description = "用户通过旧密码更新为新密码")
    public Result<Object> updatePassword(@RequestBody UpdatePasswordDto req) {
        return authBiz.updatePassword(req);
    }

}
