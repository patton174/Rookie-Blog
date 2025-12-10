package com.lx.blog.web.controller.user.auth;

import com.lx.blog.common.base.Result;
import com.lx.blog.domain.vo.UserVo;
import com.lx.blog.service.auth.biz.UserProfileBizService;
import com.lx.blog.service.auth.biz.common.UserCommonBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * @author LX
 * @date 2025/12/3
 * @description 用户个人信息控制器
 */
@Tag(name = "用户个人信息控制器", description = "用户个人信息查询操作")
@RestController
@RequestMapping("/user/profile")
@RequiredArgsConstructor
public class UserProfileController {

    @NotNull private final UserProfileBizService profileBiz;
    @NotNull private final UserCommonBizService commonBiz;

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

    /**
     * 上传用户头像
     *
     * @param file 头像文件
     * @return 头像URL
     */
    @PostMapping("/avatar")
    @Operation(summary = "上传用户头像", description = "上传并更新当前用户的头像")
    public Result<String> uploadAvatar(@RequestPart("file") MultipartFile file) {
        return commonBiz.uploadAvatar(file);
    }
}
