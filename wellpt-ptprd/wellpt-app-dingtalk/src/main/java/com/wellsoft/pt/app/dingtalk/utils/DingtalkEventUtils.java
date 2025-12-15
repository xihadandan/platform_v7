/*
 * @(#)4/23/25 V1.0
 *
 * Copyright 2025 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.dingtalk.utils;

import com.dingtalk.open.app.api.message.GenericOpenDingTalkEvent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.*;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 4/23/25.1	    zhulh		4/23/25		    Create
 * </pre>
 * @date 4/23/25
 */
public class DingtalkEventUtils {

    private static final Map<String, String> EVENT_TYPE_MAP = new HashMap<String, String>();

    private static EventQueue eventQueue = new EventQueue();

    private static Map<Long, Long> absentDepartmentIdMap = Maps.newHashMap();

    private static List<GenericOpenDingTalkEvent> events = Collections.synchronizedList(Lists.newLinkedList());

    static {
        EVENT_TYPE_MAP.put("user_add_org", "通讯录用户增加");
        EVENT_TYPE_MAP.put("user_modify_org", "通讯录用户更改");
        EVENT_TYPE_MAP.put("user_leave_org", "通讯录用户离职");
        EVENT_TYPE_MAP.put("user_active_org", "加入企业后用户激活");
        EVENT_TYPE_MAP.put("org_dept_create", "通讯录企业部门创建");
        EVENT_TYPE_MAP.put("org_dept_modify", "通讯录企业部门修改");
        EVENT_TYPE_MAP.put("org_dept_remove", "通讯录企业部门删除");
    }

    /**
     * @param eventType
     * @return
     */
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

    public static void addEvent(GenericOpenDingTalkEvent event) {
        events.add(event);
    }

    public static void removeEvent(GenericOpenDingTalkEvent event) {
        events.remove(event);
    }

    public static List<GenericOpenDingTalkEvent> getEvents() {
        return events;
    }

    public static void addAbsentDepartmentId(Long deptId) {
        absentDepartmentIdMap.put(deptId, deptId);
    }

    public static void removeAbsentDepartmentId(Object deptId) {
        absentDepartmentIdMap.remove(deptId);
    }

    public static boolean isAbsentDepartmentId(Object deptId) {
        return absentDepartmentIdMap.containsKey(deptId);
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
