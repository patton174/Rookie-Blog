package com.lx.blog.common.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author LX
 * @date 2025/12/9
 * @description 存储平台枚举类
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum StoragePlatformEnum {

    MINIO(1, "MinIO", "MinIO", "MinIO存储平台"),
    QINIU(2, "七牛云", "Qiniu", "七牛云存储平台");

    /**
     * 编码
     */
    private final Integer code;

    /**
     * 描述
     */
    private final String desc;

    /**
     * 描述
     */
    private final String desc_en;

    /**
     * 备注
     */
    private final String remark;

    public static StoragePlatformEnum byCode(Integer code) {
        if (code == null) {
            return null;
        }

        for (StoragePlatformEnum storagePlatformEnum : StoragePlatformEnum.values()) {
            if (storagePlatformEnum.getCode().equals(code)) {
                return storagePlatformEnum;
            }
        }
        return null;
    }
}
