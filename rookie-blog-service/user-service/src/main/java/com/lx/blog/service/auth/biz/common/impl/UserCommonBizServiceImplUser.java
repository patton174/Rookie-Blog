package com.lx.blog.service.auth.biz.common.impl;

import com.lx.blog.repository.dao.UserDao;
import com.lx.blog.service.auth.biz.common.UserCommonBizService;
import com.lx.blog.service.biz.UserBaseBizService;
import org.springframework.stereotype.Service;

/**
 * @author LX
 * @date 2025/12/3
 * @description 用户公共业务服务实现类
 */
@Service
public class UserCommonBizServiceImplUser extends UserBaseBizService implements UserCommonBizService {
    public UserCommonBizServiceImplUser(UserDao userDao) {
        super(userDao);
    }
}
