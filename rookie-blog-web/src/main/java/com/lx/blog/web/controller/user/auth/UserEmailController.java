package com.lx.blog.web.controller.user.auth;

import com.lx.blog.common.base.Result;
import com.lx.blog.service.auth.biz.UserEmailBizService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author LX
 * @date 2025/12/3
 * @description 用户邮箱控制器
 */
@Tag(name = "用户邮箱控制器", description = "用户邮箱查询操作")
@RestController
@RequestMapping("/user/email")
@RequiredArgsConstructor
public class UserEmailController {

    @NotNull private final UserEmailBizService emailBiz;

    /**
     * 是否已完成邮箱验证
     *
     * @return 是否已验证
     */
    @GetMapping("/verification/status")
    @Operation(summary = "检查邮箱验证状态", description = "检查当前登录用户的邮箱是否已验证")
    public Result<Boolean> isEmailVerified() {
        return emailBiz.isEmailVerified();
    }

    /**
     * 申请邮箱验证（发送验证邮件）
     *
     * @return 结果
     */
    @PostMapping("/verification/request")
    @Operation(summary = "申请邮箱验证", description = "用户通过邮箱申请验证，系统会发送验证邮件")
    public Result<Object> requestEmailVerification() {
        return emailBiz.requestEmailVerification();
    }

    /**
     * 确认邮箱验证
     *
     * @param token 临时验证令牌
     * @return 结果
     */
    @PostMapping("/verification/confirm")
    @Operation(summary = "确认邮箱验证", description = "用户通过邮箱验证令牌确认邮箱验证")
    public Result<Object> confirmEmailVerification(@RequestParam("token") String token) {
        return emailBiz.confirmEmailToken(token);
    }

}
