package com.lx.blog.common.config.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author LX
 * @date 2025/11/14
 * @description IP地址实体类
 */
@Data
@AllArgsConstructor
public class IpDomain {

    private Boolean cdnIP;

    private Boolean xcdnIP;

    private String isp;

    private String region;

}
