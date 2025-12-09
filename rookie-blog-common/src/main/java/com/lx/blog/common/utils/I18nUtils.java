package com.lx.blog.common.utils;

import com.lx.blog.common.base.ResultCode;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author LX
 * @date 2025/12/2
 * @description I18n工具类（静态方法）
 */
@Component
public class I18nUtils implements ApplicationContextAware {

    private static MessageSource messageSource;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        messageSource = applicationContext.getBean(MessageSource.class);
    }

    /**
     * 获取国际化消息
     * @param code 消息码
     * @return 国际化消息
     */
    public static String t(String code) {
        return messageSource.getMessage(code, null, LocaleContextHolder.getLocale());
    }

    /**
     * 获取国际化消息
     * @param code 消息码
     * @param args 参数
     * @return 国际化消息
     */
    public static String t(String code, Object... args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }

    /**
     * 获取国际化消息（如果消息码不存在，则返回默认消息）
     * @param code 消息码
     * @param defaultMessage 默认消息
     * @return 国际化消息
     */
    public static String tOrDefault(String code, String defaultMessage) {
        return messageSource.getMessage(code, null, defaultMessage, LocaleContextHolder.getLocale());
    }

    /**
     * 获取国际化消息（如果消息码不存在，则返回默认消息）
     * @param code 消息码
     * @param defaultMessage 默认消息
     * @param args 参数
     * @return 国际化消息
     */
    public static String tOrDefault(String code, String defaultMessage, Object... args) {
        return messageSource.getMessage(code, args, defaultMessage, LocaleContextHolder.getLocale());
    }

    /**
     * 获取国际化消息（根据指定的Locale）
     * @param code 消息码
     * @param locale 区域设置
     * @return 国际化消息
     */
    public static String tLocale(String code, Locale locale) {
        return messageSource.getMessage(code, null, locale);
    }

    /**
     * 获取国际化消息（根据指定的Locale）
     * @param code 消息码
     * @param locale 区域设置
     * @param args 参数
     * @return 国际化消息
     */
    public static String tLocale(String code, Locale locale, Object... args) {
        return messageSource.getMessage(code, args, locale);
    }

    /**
     * 获取国际化消息（根据ResultCode）
     * @param resultCode 结果码
     * @return 国际化消息
     */
    public static String from(ResultCode resultCode) {
        try {
            return messageSource.getMessage(resultCode.getMessageKey(), null, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return resultCode.getErrMsg();
        }
    }

    /**
     * 获取国际化消息（根据ResultCode）
     * @param resultCode 结果码
     * @param args 参数
     * @return 国际化消息
     */
    public static String from(ResultCode resultCode, Object... args) {
        try {
            return messageSource.getMessage(resultCode.getMessageKey(), args, LocaleContextHolder.getLocale());
        } catch (NoSuchMessageException e) {
            return resultCode.getErrMsg();
        }
    }

    /**
     * 设置当前线程的Locale
     * @param locale 区域设置
     */
    public static void setLocale(Locale locale) {
        LocaleContextHolder.setLocale(locale);
    }
}
