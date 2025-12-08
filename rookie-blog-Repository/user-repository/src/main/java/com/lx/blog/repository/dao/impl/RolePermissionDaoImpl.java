package com.lx.blog.repository.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.blog.repository.dao.RolePermissionDao;
import com.lx.blog.repository.dao.impl.mapper.RolePermissionMapper;
import com.lx.blog.repository.dao.impl.mapper.entity.RolePermission;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author LX
 * @date 2025/11/14
 * @description 角色权限数据访问实现
 */
@Repository
public class RolePermissionDaoImpl extends ServiceImpl<RolePermissionMapper, RolePermission> implements RolePermissionDao {

    /**
     * 根据角色ID查询权限ID列表
     *
     * @param roleId 角色ID
     * @return 权限ID集合
     */
    @Override
    public List<String> listPermissionIdsByRoleId(String roleId) {
        return baseMapper.selectList(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getRoleId, roleId))
                .stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
    }
}