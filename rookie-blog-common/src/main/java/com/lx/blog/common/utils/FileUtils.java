package com.lx.blog.common.utils;

import com.lx.blog.common.base.ResultCode;
import com.lx.blog.common.exception.ValidationException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * @author LX
 * @date 2025/12/9
 * @description 文件工具类
 */
public class FileUtils {

    /**
     * 获取文件扩展名
     *
     * @param fileName 文件名
     * @return 扩展名 (不带.)
     */
    public static String getExtension(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return StringUtils.EMPTY;
        }
        int index = fileName.lastIndexOf(".");
        return index == -1 ? StringUtils.EMPTY : fileName.substring(index + 1);
    }

    /**
     * 获取文件名称 (不带路径)
     *
     * @param fileName 文件名
     * @return 文件名称
     */
    public static String getName(String fileName) {
        if (fileName == null) {
            return null;
        }
        int lastUnixPos = fileName.lastIndexOf('/');
        int lastWindowsPos = fileName.lastIndexOf('\\');
        int index = Math.max(lastUnixPos, lastWindowsPos);
        return fileName.substring(index + 1);
    }

    /**
     * 生成日期路径
     *
     * @return 日期路径 (yyyy/MM/dd)
     */
    public static String datePath() {
        return DateFormatUtils.format(new Date(), "yyyy/MM/dd");
    }

    /**
     * 生成唯一文件名
     *
     * @param fileName 原文件名
     * @return 唯一文件名 (UUID.ext)
     */
    public static String uuidName(String fileName) {
        String extension = getExtension(fileName);
        String uuid = UUID.randomUUID().toString().replace("-", "");
        return StringUtils.isEmpty(extension) ? uuid : uuid + "." + extension;
    }

    /**
     * 生成完整上传路径
     *
     * @param prefix   前缀 (如: avatar)
     * @param fileName 原文件名
     * @return 完整路径 (prefix/yyyy/MM/dd/uuid.ext)
     */
    public static String pathName(String prefix, String fileName) {
        if (StringUtils.isBlank(prefix)) {
            prefix = "";
        } else if (!prefix.endsWith("/")) {
            prefix = prefix + "/";
        }
        return prefix + datePath() + "/" + uuidName(fileName);
    }

    /**
     * 校验文件是否允许下载
     *
     * @param resource 文件名称
     * @return true 正常 false 非法
     */
    public static boolean checkAllowDownload(String resource) {
        // 禁止目录上跳级别
        if (StringUtils.contains(resource, "..")) {
            return false;
        }
        // 检查允许下载的文件规则...
        return true;
    }

    /**
     * 格式化文件大小
     *
     * @param size 文件大小
     * @return 格式化后的字符串
     */
    public static String formatSize(long size) {
        long kb = 1024;
        long mb = kb * 1024;
        long gb = mb * 1024;
        if (size >= gb) {
            return String.format("%.2f GB", (float) size / gb);
        } else if (size >= mb) {
            return String.format("%.2f MB", (float) size / mb);
        } else if (size >= kb) {
            return String.format("%.2f KB", (float) size / kb);
        } else {
            return String.format("%d B", size);
        }
    }

    /**
     * 校验文件大小
     * @param size 文件大小
     * @param maxSize 最大限制 (MB)
     */
    public static void checkSize(long size, int maxSize) {
        if (size > (long) maxSize * 1024 * 1024) {
            throw new ValidationException("文件大小超出限制: " + maxSize + "MB");
        }
    }
}
