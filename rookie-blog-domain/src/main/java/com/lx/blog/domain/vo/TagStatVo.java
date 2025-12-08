package com.lx.blog.domain.vo;

import lombok.Data;

/**
 * @author LX
 * @date 2025/12/05
 * @description 标签统计VO
 */
@Data
public class TagStatVo {
    private String id;
    private String name;
    private String code;
    private Long articleCount;
}
