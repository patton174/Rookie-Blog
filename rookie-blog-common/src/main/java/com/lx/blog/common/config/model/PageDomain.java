package com.lx.blog.common.config.model;

import lombok.Data;

/**
 * @author LX
 * @date 2025/11/13
 * @description 分页参数模型
 */
@Data
public class PageDomain {

    /**
     * 当前记录起始索引
     */
    private Integer pageNum;

    /**
     * 每页显示记录数
     */
    private Integer pageSize;

    /**
     * 排序列
     */
    private String orderBy;

    /**
     * 排序的方向desc或者asc
     */
    private String isAsc = "asc";

    /**
     * 分页参数合理化
     */
    private Boolean reasonable = true;


    public String getIsAsc(Boolean isAsc) {
        return isAsc ? "asc" : "desc";
    }

}
