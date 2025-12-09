package com.lx.blog.common.utils;

import com.lx.blog.common.base.BaseException;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * @author LX
 * @date 2025/11/14
 * @description HTTP工具类
 */
public class HttpUtils {

    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    // 默认配置
    private static final int DEFAULT_CONNECT_TIMEOUT = 5000; // 5秒
    private static final int DEFAULT_READ_TIMEOUT = 30000;   // 30秒
    private static final String DEFAULT_CHARSET = "UTF-8";

    /**
     * 发送 GET 请求
     *
     * @param url 请求地址
     * @return 响应内容
     * @throws IOException 网络异常
     */
    public static String doGet(String url) throws IOException {
        return doGet(url, null, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * 发送 GET 请求
     *
     * @param url 请求地址
     * @param params 请求参数
     * @return 响应内容
     * @throws IOException 网络异常
     */
    public static String doGet(String url, Map<String, String> params) throws IOException {
        return doGet(url, params, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * 发送 GET 请求
     *
     * @param url 请求地址
     * @param params 请求参数
     * @param connectTimeout 连接超时时间(毫秒)
     * @param readTimeout 读取超时时间(毫秒)
     * @return 响应内容
     * @throws IOException 网络异常
     */
    public static String doGet(String url, Map<String, String> params,
                               int connectTimeout, int readTimeout) throws IOException {
        String fullUrl = buildUrlWithParams(url, params);
        logger.debug("GET Request: {}", fullUrl);

        HttpURLConnection connection = null;
        try {
            connection = createConnection(fullUrl, "GET", connectTimeout, readTimeout);
            connection.connect();

            return handleResponse(connection);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 发送 POST 请求 (表单格式)
     *
     * @param url 请求地址
     * @param params 请求参数
     * @return 响应内容
     * @throws IOException 网络异常
     */
    public static String doPost(String url, Map<String, String> params) throws IOException {
        return doPost(url, params, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * 发送 POST 请求 (表单格式)
     *
     * @param url 请求地址
     * @param params 请求参数
     * @param connectTimeout 连接超时时间(毫秒)
     * @param readTimeout 读取超时时间(毫秒)
     * @return 响应内容
     * @throws IOException 网络异常
     */
    public static String doPost(String url, Map<String, String> params,
                                int connectTimeout, int readTimeout) throws IOException {
        logger.debug("POST Request: {}", url);

        HttpURLConnection connection = null;
        try {
            connection = createConnection(url, "POST", connectTimeout, readTimeout);
            connection.setDoOutput(true);

            // 设置表单格式
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // 写入参数
            if (params != null && !params.isEmpty()) {
                String postData = buildFormData(params);
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(postData.getBytes(DEFAULT_CHARSET));
                }
            }

            return handleResponse(connection);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 发送 POST 请求 (JSON格式)
     *
     * @param url 请求地址
     * @param jsonBody JSON请求体
     * @return 响应内容
     * @throws IOException 网络异常
     */
    public static String doPostJson(String url, String jsonBody) throws IOException {
        return doPostJson(url, jsonBody, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * 发送 POST 请求 (JSON格式)
     *
     * @param url 请求地址
     * @param jsonBody JSON请求体
     * @param connectTimeout 连接超时时间(毫秒)
     * @param readTimeout 读取超时时间(毫秒)
     * @return 响应内容
     * @throws IOException 网络异常
     */
    public static String doPostJson(String url, String jsonBody,
                                    int connectTimeout, int readTimeout) throws IOException {
        logger.debug("POST JSON Request: {}", url);

        HttpURLConnection connection = null;
        try {
            connection = createConnection(url, "POST", connectTimeout, readTimeout);
            connection.setDoOutput(true);

            // 设置JSON格式
            connection.setRequestProperty("Content-Type", "application/json; charset=utf-8");

            // 写入JSON数据
            if (jsonBody != null) {
                try (OutputStream os = connection.getOutputStream()) {
                    os.write(jsonBody.getBytes(DEFAULT_CHARSET));
                }
            }

            return handleResponse(connection);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 发送 POST 请求 (文件上传)
     *
     * @param url 请求地址
     * @param params 文本参数
     * @param fileParams 文件参数
     * @return 响应内容
     * @throws IOException 网络异常
     */
    public static String doPostWithFiles(String url, Map<String, String> params,
                                         Map<String, File> fileParams) throws IOException {
        return doPostWithFiles(url, params, fileParams, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * 发送 POST 请求 (文件上传)
     *
     * @param url 请求地址
     * @param params 文本参数
     * @param fileParams 文件参数
     * @param connectTimeout 连接超时时间(毫秒)
     * @param readTimeout 读取超时时间(毫秒)
     * @return 响应内容
     * @throws IOException 网络异常
     */
    public static String doPostWithFiles(String url, Map<String, String> params,
                                         Map<String, File> fileParams,
                                         int connectTimeout, int readTimeout) throws IOException {
        logger.debug("POST File Upload Request: {}", url);

        String boundary = "----WebKitFormBoundary" + System.currentTimeMillis();
        HttpURLConnection connection = null;

        try {
            connection = createConnection(url, "POST", connectTimeout, readTimeout);
            connection.setDoOutput(true);

            // 设置multipart格式
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

            try (OutputStream outputStream = connection.getOutputStream();
                 PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, DEFAULT_CHARSET), true)) {

                // 写入文本参数
                if (params != null) {
                    for (Map.Entry<String, String> entry : params.entrySet()) {
                        writer.append("--").append(boundary).append("\r\n");
                        writer.append("Content-Disposition: form-data; name=\"").append(entry.getKey()).append("\"\r\n");
                        writer.append("\r\n");
                        writer.append(entry.getValue()).append("\r\n");
                    }
                }

                // 写入文件参数
                if (fileParams != null) {
                    for (Map.Entry<String, File> entry : fileParams.entrySet()) {
                        File file = entry.getValue();
                        if (file.exists() && file.isFile()) {
                            writer.append("--").append(boundary).append("\r\n");
                            writer.append("Content-Disposition: form-data; name=\"").append(entry.getKey())
                                    .append("\"; filename=\"").append(file.getName()).append("\"\r\n");
                            writer.append("Content-Type: ").append(URLConnection.guessContentTypeFromName(file.getName())).append("\r\n");
                            writer.append("\r\n");
                            writer.flush();

                            try (FileInputStream inputStream = new FileInputStream(file)) {
                                byte[] buffer = new byte[4096];
                                int bytesRead;
                                while ((bytesRead = inputStream.read(buffer)) != -1) {
                                    outputStream.write(buffer, 0, bytesRead);
                                }
                                outputStream.flush();
                            }
                            writer.append("\r\n");
                        }
                    }
                }

                writer.append("--").append(boundary).append("--\r\n");
            }

            return handleResponse(connection);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 下载文件
     *
     * @param url 文件地址
     * @param savePath 保存路径
     * @return 是否下载成功
     * @throws IOException 网络异常或文件操作异常
     */
    public static boolean downloadFile(String url, String savePath) throws IOException {
        return downloadFile(url, savePath, DEFAULT_CONNECT_TIMEOUT, DEFAULT_READ_TIMEOUT);
    }

    /**
     * 下载文件
     *
     * @param url 文件地址
     * @param savePath 保存路径
     * @param connectTimeout 连接超时时间(毫秒)
     * @param readTimeout 读取超时时间(毫秒)
     * @return 是否下载成功
     * @throws IOException 网络异常或文件操作异常
     */
    public static boolean downloadFile(String url, String savePath,
                                       int connectTimeout, int readTimeout) throws IOException {
        logger.debug("Download File: {} -> {}", url, savePath);

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        FileOutputStream outputStream = null;

        try {
            connection = createConnection(url, "GET", connectTimeout, readTimeout);
            connection.connect();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = connection.getInputStream();
                outputStream = new FileOutputStream(savePath);

                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                logger.debug("File downloaded successfully: {}", savePath);
                return true;
            } else {
                logger.error("Download failed. Response code: {}", responseCode);
                return false;
            }
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    logger.warn("Error closing input stream", e);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    logger.warn("Error closing output stream", e);
                }
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // 私有辅助方法

    private static HttpURLConnection createConnection(String url, String method,
                                                      int connectTimeout, int readTimeout) throws IOException {
        URL urlObj = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();

        connection.setRequestMethod(method);
        connection.setConnectTimeout(connectTimeout);
        connection.setReadTimeout(readTimeout);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Java HTTP Client)");
        connection.setRequestProperty("Accept", "*/*");

        return connection;
    }

    private static String buildUrlWithParams(String url, Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return url;
        }

        StringBuilder urlBuilder = new StringBuilder(url);
        if (!url.contains("?")) {
            urlBuilder.append("?");
        } else if (!url.endsWith("?")) {
            urlBuilder.append("&");
        }

        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                urlBuilder.append(URLEncoder.encode(entry.getKey(), DEFAULT_CHARSET))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), DEFAULT_CHARSET))
                        .append("&");
            }
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Unsupported encoding: " + DEFAULT_CHARSET, e);
        }

        // 删除最后一个多余的"&"
        String result = urlBuilder.toString();
        if (result.endsWith("&")) {
            result = result.substring(0, result.length() - 1);
        }

        return result;
    }

    private static String buildFormData(Map<String, String> params) {
        if (params == null || params.isEmpty()) {
            return "";
        }

        StringBuilder formData = new StringBuilder();
        try {
            for (Map.Entry<String, String> entry : params.entrySet()) {
                if (!formData.isEmpty()) {
                    formData.append("&");
                }
                formData.append(URLEncoder.encode(entry.getKey(), DEFAULT_CHARSET))
                        .append("=")
                        .append(URLEncoder.encode(entry.getValue(), DEFAULT_CHARSET));
            }
        } catch (UnsupportedEncodingException e) {
            throw new BaseException("Unsupported encoding: " + DEFAULT_CHARSET, e);
        }

        return formData.toString();
    }

    private static String handleResponse(HttpURLConnection connection) throws IOException {
        int responseCode = connection.getResponseCode();
        logger.debug("Response Code: {}", responseCode);

        InputStream inputStream;
        if (responseCode >= 200 && responseCode < 300) {
            inputStream = connection.getInputStream();
        } else {
            inputStream = connection.getErrorStream();
        }

        if (inputStream == null) {
            throw new IOException("No response body received. Response code: " + responseCode);
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            String responseBody = response.toString();
            logger.debug("Response Body: {}", responseBody);

            if (responseCode >= 200 && responseCode < 300) {
                return responseBody;
            } else {
                throw new IOException("HTTP Error: " + responseCode + " - " + responseBody);
            }
        }
    }

    /**
     * 设置全局超时时间
     */
    public static class Config {
        @Getter
        private static int globalConnectTimeout = DEFAULT_CONNECT_TIMEOUT;
        @Getter
        private static int globalReadTimeout = DEFAULT_READ_TIMEOUT;
    }

}
