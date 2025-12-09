package com.lx.blog.common.service;

import cn.dev33.satoken.stp.StpUtil;
import com.github.pagehelper.PageInfo;
import com.lx.blog.common.base.Base;
import com.lx.blog.common.base.ResultCode;
import com.lx.blog.common.utils.BeanCopyUtils;
import com.lx.blog.common.utils.I18nUtils;
import com.lx.blog.common.utils.PageUtils;
import com.lx.blog.common.utils.UUIDUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/**
 * @author LX
 * @date 2025/12/9
 * @description
 */
@Slf4j
public class BaseBiz extends Base {

    /**
     * 生成UUID
     *
     * @return UUID
     */
    public String getId(){
        return UUIDUtils.getId();
    }

    /**
     * 获取当前登录用户ID
     *
     * @return 当前登录用户ID
     */
    public String getUserId() {
        return StpUtil.getLoginIdAsString();
    }

    /**
     * 获取当前语言的国际化信息
     *
     * @param code 编码
     * @return 提示信息
     */
    public String I18n(String code, Object... args) {
        String result = I18nUtils.t(code, args);
        if (StringUtils.hasText(result)) {
            return result;
        }
        return code;
    }

    /**
     * 获取当前语言的国际化信息
     *
     * @param resultCode 结果编码
     * @return 提示信息
     */
    public String I18n(ResultCode resultCode) {
        return I18nUtils.from(resultCode);
    }

    /**
     * 获取当前语言的国际化信息
     *
     * @param resultCode 结果编码
     * @param args       参数
     * @return 提示信息
     */
    public String I18n(ResultCode resultCode, Object... args) {
        return I18nUtils.from(resultCode, args);
    }

    /**
     * 获取当前语言的国际化信息
     *
     * @param codes 编码列表
     * @return 提示信息
     */
    public String I18n(List<String> codes) {
        List<String> resultList = new ArrayList<>();
        for (String code : codes) {
            String message = I18nUtils.t(code);
            resultList.add(StringUtils.hasText(message) ? message : code);
        }
        return String.join(",", resultList);
    }

    /**
     * 复制属性
     *
     * @param source 源对象
     * @param target 目标对象
     */
    public <T> void copyProperties(Object source, T target) {
        BeanCopyUtils.copyProperties(source, target);
    }

    /**
     * 复制属性
     *
     * @param source 源对象
     * @param targetClass 目标类
     * @return 目标对象
     */
    public <T> T copyProperties(Object source, Class<T> targetClass) {
        return BeanCopyUtils.copyProperties(source, targetClass);
    }

    /**
     * 复制列表属性
     *
     * @param sourceList 源对象列表
     * @param targetClass 目标类
     * @return 目标对象列表
     */
    public <T> List<T> copyList(List<?> sourceList, Class<T> targetClass) {
        return BeanCopyUtils.copyList(sourceList, targetClass);
    }

    /**
     * 获取分页数据
     *
     * @param supplier 数据提供者（Lambda表达式，执行实际的查询逻辑）
     * @param targetClass 目标VO类
     * @param <T> 源数据类型
     * @param <R> 目标数据类型
     * @return 分页结果
     */
    public <T, R> List<R> getPage(Supplier<List<T>> supplier, Class<R> targetClass) {
        PageUtils.startPage();
        List<T> list = supplier.get();
        PageInfo<T> pageInfo = new PageInfo<>(list);
        PageUtils.clearPage();
        List<R> resultList = copyList(list, targetClass);
        PageInfo<R> resultPageInfo = new PageInfo<>(resultList);
        resultPageInfo.setTotal(pageInfo.getTotal());
        resultPageInfo.setPageNum(pageInfo.getPageNum());
        resultPageInfo.setPageSize(pageInfo.getPageSize());
        resultPageInfo.setPages(pageInfo.getPages());
        return resultList;
    }


    /**
     * 系统繁忙
     *
     * @return 系统繁忙提示信息
     */
    public String toI18nException() {
        return I18n("common.exception");
    }

    /**
     * 操作成功
     *
     * @return 操作成功提示信息
     */
    public String toI18nSuccess() {
        return I18n("common.success");
    }

    /**
     * 操作失败
     *
     * @return 操作失败提示信息
     */
    public String toI18nFail() {
        return I18n("common.fail");
    }

    /**
     * 没有此权限
     *
     * @return 没有此权限提示信息
     */
    public String toI18nDisabled() {
        return I18n("common.disabled");
    }

}
