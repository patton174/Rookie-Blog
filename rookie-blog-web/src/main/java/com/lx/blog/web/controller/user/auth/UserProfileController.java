package com.lx.blog.web.controller.user.auth;

import com.lx.blog.common.response.Result;
import com.lx.blog.domain.vo.UserVo;
import com.lx.blog.service.auth.biz.UserProfileBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @author 李旭
 * @date 2025/12/3
 * @description 用户个人信息控制器
 */
@Tag(name = "用户个人信息控制器", description = "用户个人信息查询操作")
@RestController
@RequestMapping("/user/profile")
@RequiredArgsConstructor
public class UserProfileController {

    @NotNull private final UserProfileBizService profileBiz;

    /**
     * 获取当前登录用户信息
     *
     * @return 当前登录用户信息
     */
    @GetMapping("/info")
    @Operation(summary = "获取当前登录用户信息", description = "获取当前登录用户的详细信息")
    public Result<UserVo> getProfile() {
        return profileBiz.getProfile();
    }

    /**
     * 根据用户ID获取用户名
     *
     * @param userId 用户ID
     * @return 用户名
     */
    @GetMapping("/public/username/{userId}")
    @Operation(summary = "根据用户ID获取用户名", description = "根据用户ID查询用户名")
    public Result<Map<String, String>> getUsernameById(@PathVariable @NotNull String userId) {
        return profileBiz.getUsernameById(userId);
    }
}
