package com.lx.blog.common.aop.log;

import cn.dev33.satoken.stp.StpUtil;
import com.lx.blog.common.utils.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author LX
 * @date 2025/12/03
 * @description 操作日志切面
 */
@Aspect
@Component
@RequiredArgsConstructor
public class OpLogAspect {

    @NotNull private final OpLogRegistry registry;
    @NotNull private final ApplicationEventPublisher publisher;

    @AfterReturning(pointcut = "@annotation(opLog)", returning = "ret", argNames = "jp,opLog,ret")
    public void afterSuccess(JoinPoint jp, OpLog opLog, Object ret) {
        MethodSignature ms = (MethodSignature) jp.getSignature();
        String className = ms.getDeclaringTypeName();
        String methodName = ms.getName();
        String funcId = opLog.func().isEmpty() ? className + "." + methodName : opLog.func();

        // 查找匹配的表
        for (Map.Entry<String, List<String>> e : registry.getTableFunctions().entrySet()) {
            if (e.getValue().contains(funcId)) {
                String table = e.getKey();
                Map<String, Object> payload = buildPayload(opLog, jp, ret);
                publisher.publishEvent(new OpLogEvent(this, table, payload));
            }
        }
    }

    private Map<String, Object> buildPayload(OpLog opLog, JoinPoint jp, Object ret) {
        Map<String, Object> map = new HashMap<>();
        HttpServletRequest req = ServletUtils.getRequest();
        String ip = req.getRemoteAddr();
        String ua = req.getHeader("User-Agent");
        String referer = req.getHeader("Referer");

        String userId = null;
        try { userId = StpUtil.getLoginIdAsString(); } catch (Exception ignored) {}

        map.put("_class", opLog.func());
        map.put("action", opLog.action().isEmpty() ? jp.getSignature().getName() : opLog.action());
        map.put("userId", userId);
        map.put("ip", ip);
        map.put("user_agent", ua);
        map.put("referer", referer);
        map.put("logged_at", LocalDateTime.now());
        map.put("params", ServletUtils.getParamsMap());
        map.put("params_array", ServletUtils.getParamsArrayMap());
        map.put("ret", ret);
        return map;
    }
}
