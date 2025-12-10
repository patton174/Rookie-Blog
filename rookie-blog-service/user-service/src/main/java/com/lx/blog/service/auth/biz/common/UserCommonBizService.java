package com.lx.blog.service.auth.biz.common;

import com.lx.blog.common.base.Result;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author LX
 * @date 2025/12/3
 * @description 用户公共业务服务接口
 */
public interface UserCommonBizService {

    /**
     * 上传用户头像
     *
     * @param file 头像文件
     * @return 访问URL
     */
    Result<String> uploadAvatar(MultipartFile file);
}
