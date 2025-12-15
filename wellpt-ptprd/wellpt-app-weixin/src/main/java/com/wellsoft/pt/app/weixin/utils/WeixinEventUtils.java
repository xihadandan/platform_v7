/*
 * @(#)5/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.weixin.utils;

import com.google.common.collect.Maps;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 5/23/25.1	    zhulh		5/23/25		    Create
 * </pre>
 * @date 5/23/25
 */
public class WeixinEventUtils {
    private static final Map<String, String> EVENT_TYPE_MAP = new HashMap<String, String>();

    private static EventQueue eventQueue = new EventQueue();

    static {
        EVENT_TYPE_MAP.put("create_user", "新增成员事件");
        EVENT_TYPE_MAP.put("update_user", "更新成员事件");
        EVENT_TYPE_MAP.put("delete_user", "删除成员事件");
        EVENT_TYPE_MAP.put("create_party", "新增部门事件");
        EVENT_TYPE_MAP.put("update_party", "更新部门事件");
        EVENT_TYPE_MAP.put("delete_party", "删除部门事件");
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
