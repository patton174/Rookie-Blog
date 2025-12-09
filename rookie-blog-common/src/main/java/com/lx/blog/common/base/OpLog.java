package com.lx.blog.common.base;

import java.lang.annotation.*;

/**
 * @author LX
 * @date 2025/12/03
 * @description 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface OpLog {

    /**
     * 操作动作名称
     */
    String action() default "";

    /**
     * 函数标识（用于匹配tableFunctions映射），不填则使用 类名.方法名
     */
    String func() default "";
}

