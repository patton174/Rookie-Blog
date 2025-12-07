package com.lx.blog.common.utils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author LX
 * @date 2025/12/04
 * @description 高效UUID工具类 (基于时间戳与签名)
 */
public class UUIDUtils {

    private static final char[] HEX_DIGITS = "0123456789abcdef".toCharArray();

    /**
     * 生成基于时间戳和参数签名的UUID (有序，32位Hex)
     * <p>
     * 组成：时间戳Hex(约11-13位) + 参数签名Hash(截取剩余位数)
     * 适用于需要根据参数生成确定性ID或幂等Key的场景。
     * 注意：如果在同一毫秒内使用相同参数调用，生成的UUID将相同。
     * </p>
     *
     * @param params 参与签名的参数 (null或空字符串将被忽略)
     * @return 32位UUID字符串
     */
    public static String signatureUuid(Object... params) {
        return signatureUuid(System.currentTimeMillis(), params);
    }

    /**
     * 生成基于指定时间戳和参数签名的UUID
     *
     * @param timestamp 时间戳 (毫秒)
     * @param params 参与签名的参数
     * @return 32位UUID字符串
     */
    public static String signatureUuid(long timestamp, Object... params) {
        StringBuilder sb = new StringBuilder();
        if (params != null) {
            for (Object param : params) {
                sb.append(param);
            }
        }
        return signatureUuid(timestamp, sb.toString());
    }

    /**
     * 生成基于指定时间戳和签名的UUID
     *
     * @param timestamp 时间戳 (毫秒)
     * @param signature 签名/Hash字符串
     * @return 32位UUID字符串
     */
    public static String signatureUuid(long timestamp, String signature) {
        // 1. 时间戳Hex
        String timeHex = Long.toHexString(timestamp);

        // 2. 签名Hash (MD5)
        String hash = md5Hex(signature);

        // 3. 拼接 (保持32位)
        StringBuilder uuid = new StringBuilder(32);
        uuid.append(timeHex);

        int remaining = 32 - uuid.length();
        if (remaining > 0) {
            if (hash.length() >= remaining) {
                uuid.append(hash, 0, remaining);
            } else {
                uuid.append(hash);
                // 补齐
                while (uuid.length() < 32) {
                    uuid.append('0');
                }
            }
        }

        return uuid.toString();
    }

    /**
     * 计算MD5 Hex
     */
    private static String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            char[] out = new char[bytes.length * 2];
            for (int i = 0; i < bytes.length; i++) {
                int c = bytes[i] & 0xFF;
                out[i * 2] = HEX_DIGITS[c >>> 4];
                out[i * 2 + 1] = HEX_DIGITS[c & 0x0F];
            }
            return new String(out);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}

