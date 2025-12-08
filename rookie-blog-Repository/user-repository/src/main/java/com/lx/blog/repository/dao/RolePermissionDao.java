package com.lx.blog.repository.dao;

import com.baomidou.mybatisplus.extension.service.IService;
import com.lx.blog.repository.dao.impl.mapper.entity.RolePermission;

import java.util.List;

/**
 * @author LX
 * @date 2025/11/14
 * @description 角色权限数据访问层
 */
public interface RolePermissionDao extends IService<RolePermission> {

    /**
     * 根据角色ID查询权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID集合
     */
    List<String> listPermissionIdsByRoleId(String roleId);
}