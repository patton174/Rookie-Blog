package com.lx.blog.service.msg.ws;

import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author LX
 * @date 2025/12/10
 * @description WebSocket 服务端点
 */
@Slf4j
@Component
@ServerEndpoint("/ws/message/{userId}")
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。
     */
    private static final ConcurrentHashMap<String, WebSocketServer> WEB_SOCKET_MAP = new ConcurrentHashMap<>();

    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;

    /**
     * 接收 userId
     */
    private String userId;

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        if (WEB_SOCKET_MAP.containsKey(userId)) {
            WEB_SOCKET_MAP.remove(userId);
            WEB_SOCKET_MAP.put(userId, this);
        } else {
            WEB_SOCKET_MAP.put(userId, this);
        }
        log.info("用户连接: userId={}, 当前在线人数: {}", userId, WEB_SOCKET_MAP.size());
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        if (WEB_SOCKET_MAP.containsKey(userId)) {
            WEB_SOCKET_MAP.remove(userId);
            log.info("用户退出: userId={}, 当前在线人数: {}", userId, WEB_SOCKET_MAP.size());
        }
    }

    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     * @param session 会话
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到用户消息: userId={}, message={}", userId, message);
        // 可以处理心跳或回复
    }

    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("WebSocket发生错误: userId={}, error={}", userId, error.getMessage());
    }

    /**
     * 实现服务器主动推送
     */
    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }

    /**
     * 发送自定义消息
     */
    public static void sendInfo(String message, String userId) {
        log.info("推送消息到窗口: userId={}, message={}", userId, message);
        if (userId != null && WEB_SOCKET_MAP.containsKey(userId)) {
            try {
                WEB_SOCKET_MAP.get(userId).sendMessage(message);
            } catch (IOException e) {
                log.error("消息推送失败: userId={}, error={}", userId, e.getMessage());
            }
        } else {
            log.warn("用户不在线: userId={}", userId);
        }
    }
}
