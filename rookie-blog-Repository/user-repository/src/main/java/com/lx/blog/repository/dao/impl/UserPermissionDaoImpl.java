package com.lx.blog.repository.dao.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.lx.blog.repository.dao.UserPermissionDao;
import com.lx.blog.repository.dao.impl.mapper.UserPermissionMapper;
import com.lx.blog.repository.dao.impl.mapper.PermissionMapper;
import com.lx.blog.repository.dao.impl.mapper.entity.UserPermission;
import com.lx.blog.repository.dao.impl.mapper.entity.Permission;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import java.util.List;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
/**
 * @author LX
 * @date 2025/11/14
 * @description 用户权限映射器实现类
 */
@Repository
@RequiredArgsConstructor
public class UserPermissionDaoImpl extends ServiceImpl<UserPermissionMapper, UserPermission> implements UserPermissionDao {

    @NotNull private final PermissionMapper permissionMapper;

    /**
     * 赋予用户权限（grant 并幂等）
     *
     * @param userId 用户ID
     * @param permissionId 权限ID
     */
    @Override
    public void grantPermission(String userId, String permissionId) {
        // 若存在记录，更新为 grant；否则插入
        UserPermission exists = this.getOne(new LambdaQueryWrapper<UserPermission>()
                .eq(UserPermission::getUserId, userId)
                .eq(UserPermission::getPermissionId, permissionId));
        if (exists == null) {
            this.save(UserPermission.builder().userId(userId).permissionId(permissionId).effect("grant").createdAt(LocalDateTime.now()).build());
        } else if (!"grant".equalsIgnoreCase(exists.getEffect())) {
            this.update(new LambdaUpdateWrapper<UserPermission>()
                    .eq(UserPermission::getUserId, userId)
                    .eq(UserPermission::getPermissionId, permissionId)
                    .set(UserPermission::getEffect, "grant"));
        }
    }

    /**
     * 拒绝用户权限（deny 并幂等）
     *
     * @param userId 用户ID
     * @param permissionId 权限ID
     */
    @Override
    public void denyPermission(String userId, String permissionId) {
        UserPermission exists = this.getOne(new LambdaQueryWrapper<UserPermission>()
                .eq(UserPermission::getUserId, userId)
                .eq(UserPermission::getPermissionId, permissionId));
        if (exists == null) {
            this.save(UserPermission.builder().userId(userId).permissionId(permissionId).effect("deny").createdAt(LocalDateTime.now()).build());
        } else if (!"deny".equalsIgnoreCase(exists.getEffect())) {
            this.update(new LambdaUpdateWrapper<UserPermission>()
                    .eq(UserPermission::getUserId, userId)
                    .eq(UserPermission::getPermissionId, permissionId)
                    .set(UserPermission::getEffect, "deny"));
        }
    }

    /**
     * 撤销用户直授权限
     *
     * @param userId 用户ID
     * @param permissionId 权限ID
     */
    @Override
    public void revokePermission(String userId, String permissionId) {
        this.remove(new LambdaQueryWrapper<UserPermission>().eq(UserPermission::getUserId, userId).eq(UserPermission::getPermissionId, permissionId));
    }

    /**
     * 查询用户直授的权限列表
     *
     * @param userId 用户ID
     * @return 权限实体列表
     */
    @Override
    public List<Permission> listDirectPermissions(String userId) {
        List<UserPermission> ups = this.list(new LambdaQueryWrapper<UserPermission>().eq(UserPermission::getUserId, userId).eq(UserPermission::getEffect, "grant"));
        List<String> ids = ups.stream().map(UserPermission::getPermissionId).collect(Collectors.toList());
        if (ids.isEmpty()) return List.of();
        return permissionMapper.selectBatchIds(ids);
    }
}
