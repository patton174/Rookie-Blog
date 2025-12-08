package com.lx.blog.repository.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.blog.repository.dao.impl.mapper.entity.UserPermission;
import com.lx.blog.repository.dao.impl.mapper.entity.Permission;

import java.util.List;

/**
 * @author LX
 * @date 2025/11/14
 * @description 用户权限数据访问层
 */
public interface UserPermissionDao extends IService<UserPermission> {

    /**
     * 赋予用户权限（grant 并幂等）
     *
     * @param userId 用户ID
     * @param permissionId 权限ID
     */
    void grantPermission(String userId, String permissionId);

    /**
     * 拒绝用户权限（deny 并幂等）
     *
     * @param userId 用户ID
     * @param permissionId 权限ID
     */
    void denyPermission(String userId, String permissionId);

    /**
     * 撤销用户直授权限
     *
     * @param userId 用户ID
     * @param permissionId 权限ID
     */
    void revokePermission(String userId, String permissionId);

    /**
     * 查询用户直授的权限列表
     *
     * @param userId 用户ID
     * @return 权限实体列表
     */
    List<Permission> listDirectPermissions(String userId);
}
