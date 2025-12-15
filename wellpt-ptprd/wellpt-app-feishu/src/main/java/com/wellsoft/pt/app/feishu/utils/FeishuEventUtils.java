package com.wellsoft.pt.app.feishu.utils;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.lark.oapi.event.EventDispatcher;
import com.lark.oapi.service.contact.ContactService;
import com.lark.oapi.service.contact.v3.model.P2UserCreatedV3;
import com.lark.oapi.service.contact.v3.model.P2UserUpdatedV3;
import com.lark.oapi.service.contact.v3.model.UserEvent;
import com.wellsoft.pt.app.feishu.ws.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class FeishuEventUtils {
    private static final Logger log = LoggerFactory.getLogger(FeishuEventUtils.class);

    private static final Map<String, String> EVENT_TYPE_MAP = new HashMap<String, String>();

    private static EventQueue eventQueue = new EventQueue();

    static {
        EVENT_TYPE_MAP.put("contact.user.created_v3", "员工入职");
        EVENT_TYPE_MAP.put("contact.user.deleted_v3", "员工离职");
        EVENT_TYPE_MAP.put("contact.user.updated_v3", "员工信息被修改");
        EVENT_TYPE_MAP.put("contact.department.created_v3", "部门新建");
        EVENT_TYPE_MAP.put("contact.department.deleted_v3", "部门被删除");
        EVENT_TYPE_MAP.put("contact.department.updated_v3", "部门信息变化");
        EVENT_TYPE_MAP.put("im.message.receive_v1", "接收消息");
        EVENT_TYPE_MAP.put("im.chat.disbanded_v1", "群解散");
        EVENT_TYPE_MAP.put("im.chat.member.user.added_v1", "用户进群");
        EVENT_TYPE_MAP.put("im.chat.member.user.deleted_v1", "用户出群");
    }

    public static Map<String, Client> clientMap = new ConcurrentHashMap<>();


    private static EventDispatcher getEventDispatcher() {
        EventDispatcher eventDispatcher = EventDispatcher.newBuilder("", "")
                .onP2UserCreatedV3(new ContactService.P2UserCreatedV3Handler() {
                    @Override
                    public void handle(P2UserCreatedV3 p2UserCreatedV3) throws Exception {
                        log.info("用户创建事件");
                        UserEvent userEvent = p2UserCreatedV3.getEvent().getObject();
                        log.info(JSON.toJSONString(userEvent));
                    }
                })
                //用户更新事件
                .onP2UserUpdatedV3(new ContactService.P2UserUpdatedV3Handler() {
                    @Override
                    public void handle(P2UserUpdatedV3 p2UserUpdatedV3) throws Exception {
                        log.info("===1===用户更新事件");
                        log.info(JSON.toJSONString(p2UserUpdatedV3));
                        log.info("===2==用户更新事件");
                    }
                }).build();
        return eventDispatcher;
    }

    private static void startEvent(String appId, String appSecret) {
        if (clientMap.containsKey(appId)) {
            return;
        }
        log.info("start:{}", appId);
        Client client = new Client.Builder(appId, appSecret)
                .eventHandler(getEventDispatcher())
                .build();
        client.start();
        clientMap.put(appId, client);
        log.info("startend：{}", appId);
    }

    public static void stopEvent(String appId) {
        log.info("stop:{}", appId);
        Client client = clientMap.remove(appId);
        client.stop();
    }

    public static String getEventTypeName(String eventType) {
        return EVENT_TYPE_MAP.get(eventType);
    }


    public static boolean existsEvent(String eventId) {
        return eventQueue.existsEvent(eventId);
    }

    public static void addEvent(String eventId) {
        eventQueue.addEvent(eventId);
    }

    public static void removeEvent(String eventId) {
        eventQueue.removeEvent(eventId);
    }

    private static class EventQueue {

        private static Map<String, String> eventMap = Maps.newConcurrentMap();
        private Queue<String> eventQueue = new ArrayDeque<>();

        public boolean existsEvent(String eventId) {
            return eventMap.containsKey(eventId);
        }

        public void addEvent(String eventId) {
            eventMap.put(eventId, eventId);
            eventQueue.offer(eventId);
            if (eventQueue.size() > 50) {
                String removeEventId = eventQueue.poll();
                eventMap.remove(removeEventId);
            }
        }

        public void removeEvent(String eventId) {
            eventMap.remove(eventId);
            eventQueue.remove(eventId);
        }
    }

}
