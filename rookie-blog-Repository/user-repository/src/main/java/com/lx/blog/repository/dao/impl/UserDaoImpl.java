package com.lx.blog.repository.dao.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.blog.repository.dao.impl.mapper.RoleMapper;
import com.lx.blog.repository.dao.impl.mapper.RolePermissionMapper;
import com.lx.blog.repository.dao.impl.mapper.PermissionMapper;
import com.lx.blog.repository.dao.impl.mapper.UserPermissionMapper;
import com.lx.blog.repository.dao.impl.mapper.UserRoleMapper;
import com.lx.blog.repository.dao.impl.mapper.UserMapper;
import com.lx.blog.repository.dao.UserDao;
import com.lx.blog.repository.dao.impl.mapper.entity.Permission;
import com.lx.blog.repository.dao.impl.mapper.entity.Role;
import com.lx.blog.repository.dao.impl.mapper.entity.RolePermission;
import com.lx.blog.repository.dao.impl.mapper.entity.UserPermission;
import com.lx.blog.repository.dao.impl.mapper.entity.UserRole;
import com.lx.blog.repository.dao.impl.mapper.entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;
import java.time.LocalDateTime;

/**
 * @author LX
 * @date 2025/11/12
 * @description 用户映射器实现类
 */
@Repository
@RequiredArgsConstructor
public class UserDaoImpl extends ServiceImpl<UserMapper, User> implements UserDao {

    @NotNull private final UserRoleMapper userRoleMapper;
    @NotNull private final RoleMapper roleMapper;
    @NotNull private final RolePermissionMapper rolePermissionMapper;
    @NotNull private final UserPermissionMapper userPermissionMapper;
    @NotNull private final PermissionMapper permissionMapper;

    /**
     * 根据用户名获取用户
     *
     * @param username 用户名
     * @return 用户实体
     */
    @Override
    public User getUserByName(String username) {
        return baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
    }

    /**
     * 获取所有用户
     *
     * @return 用户实体列表
     */
    @Override
    public List<User> getAllUsers() {
        return baseMapper.selectList(new LambdaQueryWrapper<>());
    }

    /**
     * 根据用户ID判断用户是否启用
     *
     * @param id 用户ID
     * @return 如果用户存在且启用，则返回true；否则返回false
     */
    @Override
    public Boolean isEnabledById(String id) {
        User user = baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getId, id));
        return user != null && user.getStatus().equals(1);
    }

    /**
     * 根据邮箱获取用户信息
     *
     * @param email 邮箱
     * @return 用户信息
     */
    @Override
    public User getByEmail(String email) {
        return baseMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
    }

    /**
     * 更新用户最后登录时间与IP
     *
     * @param id 用户ID
     * @param ip 登录IP
     */
    @Override
    public void updateLastLogin(String id, String ip) {
        baseMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getId, id)
                .set(User::getLastLoginAt, LocalDateTime.now())
                .set(User::getLastLoginIp, ip));
    }

    /**
     * 查询用户的角色列表
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    @Override
    public List<Role> listRolesByUserId(String userId) {
        List<UserRole> urs = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        List<String> roleIds = urs.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        if (roleIds.isEmpty()) return List.of();
        return roleMapper.selectBatchIds(roleIds);
    }

    /**
     * 查询用户的有效权限列表（合并角色继承与直授，剔除直拒）
     *
     * @param userId 用户ID
     * @return 权限列表
     */
    @Override
    public List<Permission> listPermissionsByUserId(String userId) {
        // 角色继承的权限
        List<UserRole> urs = userRoleMapper.selectList(new LambdaQueryWrapper<UserRole>().eq(UserRole::getUserId, userId));
        List<String> roleIds = urs.stream().map(UserRole::getRoleId).collect(Collectors.toList());
        Set<String> permFromRoles = new LinkedHashSet<>();
        if (!roleIds.isEmpty()) {
            List<RolePermission> rps = rolePermissionMapper.selectList(new LambdaQueryWrapper<RolePermission>().in(RolePermission::getRoleId, roleIds));
            permFromRoles.addAll(rps.stream().map(RolePermission::getPermissionId).collect(Collectors.toSet()));
        }

        // 用户直授/直拒
        List<UserPermission> ups = userPermissionMapper.selectList(new LambdaQueryWrapper<UserPermission>().eq(UserPermission::getUserId, userId));
        Set<String> grant = ups.stream().filter(up -> "grant".equalsIgnoreCase(up.getEffect())).map(UserPermission::getPermissionId).collect(Collectors.toSet());
        Set<String> deny = ups.stream().filter(up -> "deny".equalsIgnoreCase(up.getEffect())).map(UserPermission::getPermissionId).collect(Collectors.toSet());

        // 合并：角色 + grant - deny
        LinkedHashSet<String> finalIds = new LinkedHashSet<>(permFromRoles);
        finalIds.addAll(grant);
        finalIds.removeAll(deny);
        if (finalIds.isEmpty()) return List.of();
        return permissionMapper.selectBatchIds(finalIds);
    }
}
