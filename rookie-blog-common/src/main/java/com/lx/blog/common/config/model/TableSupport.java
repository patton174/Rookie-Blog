package com.lx.blog.common.config.model;

import com.lx.blog.common.utils.ServletUtils;
import lombok.Data;

/**
 * @author LX
 * @date 2025/11/13
 * @description
 */
@Data
public class TableSupport {

    /**
     * 当前记录起始索引
     */
    public static final String PAGE_NUM = "pageNum";

    /**
     * 每页显示记录数
     */
    public static final String PAGE_SIZE = "pageSize";

    /**
     * 排序列
     */
    public static final String ORDER_BY = "orderBy";

    /**
     * 排序的方向 "desc" 或者 "asc".
     */
    public static final String IS_ASC = "isAsc";

    /**
     * 分页参数合理化
     */
    public static final String REASONABLE = "reasonable";

    /**
     * 封装分页对象
     */
    public static PageDomain getPageDomain() {
        PageDomain pageDomain = new PageDomain();
        pageDomain.setPageNum(ServletUtils.getParameterToInt(PAGE_NUM, 1));
        pageDomain.setPageSize(ServletUtils.getParameterToInt(PAGE_SIZE, 10));
        pageDomain.setOrderBy(ServletUtils.getParameterToString(ORDER_BY));
        pageDomain.setIsAsc(ServletUtils.getParameterToString(IS_ASC));
        pageDomain.setReasonable(ServletUtils.getParameterToBool(REASONABLE));
        return pageDomain;
    }

    public static PageDomain buildPageRequest() {
        return getPageDomain();
    }
}
