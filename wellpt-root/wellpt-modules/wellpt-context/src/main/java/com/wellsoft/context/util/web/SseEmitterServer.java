package com.wellsoft.context.util.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class SseEmitterServer {

    private static final Logger logger = LoggerFactory.getLogger(SseEmitterServer.class);

    /**
     * 当前连接数
     */
    private static AtomicInteger count = new AtomicInteger(0);

    /**
     * 使用map对象，便于根据userId来获取对应的SseEmitter，或者放redis里面
     */
    private static Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    /**
     * 创建用户连接并返回 SseEmitter
     *
     * @param userId 用户ID
     * @return SseEmitter
     */
    public static SseEmitter connect(String userId) {
        if (sseEmitterMap.containsKey(userId)) {
            return sseEmitterMap.get(userId);
        }
        SseEmitter sseEmitter = new SseEmitter(0L);
        sseEmitter.onCompletion(completionCallBack(userId));
        sseEmitter.onTimeout(timeoutCallBack(userId));
        sseEmitterMap.put(userId, sseEmitter);
        count.getAndIncrement();
        logger.info("创建新的sse连接，当前用户：{}", userId);
        return sseEmitter;
    }

    /**
     * 给指定用户发送信息
     *
     * @param userId
     * @param jsonMsg
     */
    public static void sendMessage(String userId, String jsonMsg) {
        try {
            SseEmitter emitter = sseEmitterMap.get(userId);
            if (emitter == null) {
                logger.warn("sse用户[{}]不在注册表，消息推送失败", userId);
                return;
            }
            emitter.send(jsonMsg, MediaType.APPLICATION_JSON);
        } catch (IOException e) {
            logger.error("sse用户[{}]推送异常:{}", userId, e.getMessage());
            removeUser(userId);
        }
    }

    /**
     * 群发消息
     *
     * @param jsonMsg
     * @param userIds
     */
    public static void batchSendMessage(String jsonMsg, List<String> userIds) {
        userIds.forEach(userId -> sendMessage(userId, jsonMsg));
    }

    /**
     * 群发所有人
     *
     * @param jsonMsg
     */
    public static void batchSendMessage(String jsonMsg) {
        sseEmitterMap.forEach((k, v) -> {
            try {
                v.send(jsonMsg, MediaType.APPLICATION_JSON);
            } catch (IOException e) {
                logger.error("用户[{}]推送异常:{}", k, e.getMessage());
                removeUser(k);
            }
        });
    }

    /**
     * 移除用户连接
     */
    public static void removeUser(String userId) {
        SseEmitter emitter = sseEmitterMap.get(userId);
        if (emitter != null) {
            emitter.complete();
        }
        sseEmitterMap.remove(userId);
        // 数量-1
        count.getAndDecrement();
        logger.info("移除sse用户：{}", userId);
    }

    /**
     * 获取当前连接信息
     */
    public static List<String> getIds() {
        return new ArrayList<>(sseEmitterMap.keySet());
    }

    /**
     * 获取当前连接数量
     */
    public static int getUserCount() {
        return count.intValue();
    }

    private static Runnable completionCallBack(String userId) {
        return () -> {
            logger.info("结束sse用户连接：{}", userId);
            removeUser(userId);
        };
    }

    private static Runnable timeoutCallBack(String userId) {
        return () -> {
            logger.info("连接sse用户超时：{}", userId);
            removeUser(userId);
        };
    }

    private static Throwable errorCallBack(String userId) {
        logger.info("sse用户连接异常：{}", userId);
        removeUser(userId);
        return new Throwable();
    }


}