package com.lx.blog.service.auth.biz.common.impl;

import com.lx.blog.common.base.Result;
import com.lx.blog.common.utils.FileUtils;
import com.lx.blog.common.utils.UUIDUtils;
import com.lx.blog.domain.dto.FileUploadDto;
import com.lx.blog.domain.vo.FileVo;
import com.lx.blog.repository.dao.UserDao;
import com.lx.blog.repository.dao.impl.mapper.entity.User;
import com.lx.blog.service.FileService;
import com.lx.blog.service.auth.biz.common.UserCommonBizService;
import com.lx.blog.service.biz.UserBaseBizService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @author LX
 * @date 2025/12/3
 * @description 用户公共业务服务实现类
 */
@Service
public class UserCommonBizServiceImplUser extends UserBaseBizService implements UserCommonBizService {

    private final FileService fileService;

    public UserCommonBizServiceImplUser(UserDao userDao, FileService fileService) {
        super(userDao);
        this.fileService = fileService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result<String> uploadAvatar(MultipartFile file) {
        String userId = getUserId();
        User user = userDao.getById(userId);
        if (user == null) {
            return Result.error("用户不存在");
        }

        try {
            // 上传头像
            // 路径：avatar/{userId}/{filename}
            String originalFilename = file.getOriginalFilename();
            String extension = FileUtils.getExtension(originalFilename);
            String filename = UUIDUtils.getId() + "." + extension;
            String path = "avatar/" + userId + "/" + filename;

            FileUploadDto uploadDto = new FileUploadDto();
            uploadDto.setContent(file.getBytes());
            uploadDto.setKey(path);
            uploadDto.setContentType(file.getContentType());
            uploadDto.setContentLength(file.getSize());

            FileVo fileVo = fileService.upload(uploadDto);
            
            // 更新用户头像
            user.setAvatarUrl(fileVo.getUrl());
            userDao.updateById(user);

            return Result.ok(fileVo.getUrl());
        } catch (IOException e) {
            throw new RuntimeException("文件读取失败", e);
        }
    }
}
