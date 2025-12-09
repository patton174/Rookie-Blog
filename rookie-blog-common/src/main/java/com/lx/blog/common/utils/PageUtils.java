package com.lx.blog.common.utils;

import com.github.pagehelper.PageHelper;
import com.lx.blog.common.config.model.PageDomain;
import com.lx.blog.common.config.model.TableSupport;

/**
 * @author LX
 * @date 2025/11/13
 * @description 分页工具类
 */
public class PageUtils extends PageHelper {

    /**
     * 开始分页
     */
    public static void startPage() {
        PageDomain pageDomain = TableSupport.getPageDomain();
        Integer pageNum = pageDomain.getPageNum();
        Integer pageSize = pageDomain.getPageSize();
        String orderBy = SqlUtils.escapeOrderBySql(pageDomain.getOrderBy());
        Boolean reasonable = pageDomain.getReasonable();
        PageHelper.startPage(pageNum, pageSize, orderBy).setReasonable(reasonable);
    }

    /**
     * 清理分页的线程变量
     */
    public static void clearPage() {
        PageHelper.clearPage();
    }
}
