package com.lx.blog.service.biz;

import com.lx.blog.common.service.BaseBiz;
import com.lx.blog.repository.dao.UserDao;
import com.lx.blog.repository.dao.impl.mapper.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;


/**
 * @author LX
 * @date 2025/12/9
 * @description 基础业务服务类 (User服务专属)
 */
@Getter
@RequiredArgsConstructor
public class BaseBizService extends BaseBiz {

    /**
     * 暴露给子类使用，减少重复注入
     */
    @NotNull
    protected final UserDao userDao;

    @Value("${app.host:http://localhost:8080}")
    protected String appHost;

    /**
     * 根据用户名或邮箱获取用户
     *
     * @param key 用户名或邮箱
     * @return 用户实体
     */
    public User getUser(String key) {
        User user = userDao.getUserByName(key);
        if (user != null) {
            return user;
        }
        return userDao.getByEmail(key);
    }
}
