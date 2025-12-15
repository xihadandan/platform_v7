/*
 * @(#)2018年3月1日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.facade.service.impl;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.wellsoft.context.component.select2.Select2DataBean;
import com.wellsoft.context.component.select2.Select2GroupData;
import com.wellsoft.context.component.select2.Select2QueryInfo;
import com.wellsoft.context.service.AbstractApiFacade;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.calendar.bean.CalendarRights;
import com.wellsoft.pt.app.calendar.entity.MyCalendarEntity;
import com.wellsoft.pt.app.calendar.entity.MyCalendarEventEntity;
import com.wellsoft.pt.app.calendar.facade.service.CalendarFacade;
import com.wellsoft.pt.app.calendar.service.MyCalendarEventService;
import com.wellsoft.pt.app.calendar.service.MyCalendarService;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Description: 日程管理服务实现类
 *
 * @author zyguo
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月1日.1	zyguo		2018年3月1日		Create
 * </pre>
 * @date 2018年3月1日
 */
@Service
public class CalendarFacadeImpl extends AbstractApiFacade implements CalendarFacade {

    @Resource
    MyCalendarService myCalendarService;
    @Resource
    MyCalendarEventService myCalendarEventService;

    @Resource
    OrgApiFacade orgApiFacade;

    @Override
    public List<MyCalendarEntity> queryAppMyCalendarList() {
        return myCalendarService.queryAppMyCalendarList();
    }

    @Override
    public List<MyCalendarEntity> queryAppMyCalendarListAndCheckDefaultCalendar() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        MyCalendarEntity calendar = myCalendarService.getDefaultCalendar(userId);
        // 没有默认日历本，需要新建
        if (calendar == null) {
            MyCalendarEntity c = new MyCalendarEntity();
            c.setCalendarName("默认日历本");
            c.setIsDefault(1);
            c.setCode("00001");
            JSONObject j = new JSONObject();
            try {
                j.put("0", "不公开");
                c.setPublicRange(j.toString());
                c.setPublicRangeValue("0");
                c.setPublicRangeShow("不公开");
            } catch (JSONException e) {
            }
            myCalendarService.save(c);
        }
        final List<MyCalendarEntity> defaultList = Lists.newArrayList();// 默认日历本
        Predicate<MyCalendarEntity> preFilter = new Predicate<MyCalendarEntity>() {
            @Override
            public boolean apply(MyCalendarEntity myCalendarEntity) {
                if (StringUtils.equals("默认日历本", myCalendarEntity.getCalendarName())) {
                    defaultList.add(myCalendarEntity);
                    return false;
                }
                return true;
            }
        };
        List<MyCalendarEntity> allList = myCalendarService.queryAppMyCalendarList();// 所有数据
        Collection<MyCalendarEntity> list = Collections2.filter(allList, preFilter);// 过滤默认日历表
        Ordering<MyCalendarEntity> ordering = Ordering.natural().nullsLast()// 按照创建时间顺序排序
                .onResultOf(new Function<MyCalendarEntity, Date>() {
                    public Date apply(MyCalendarEntity myCalendarEntity) {
                        return myCalendarEntity.getCreateTime();
                    }
                });
        List<MyCalendarEntity> orderedList = ordering.sortedCopy(list);
        if (CollectionUtils.isNotEmpty(defaultList)) {
            orderedList.add(0, defaultList.get(0));
        }
        return orderedList;
    }

    // 我管理的他人日历本
    @Override
    public List<MyCalendarEntity> queryMyManageCalendarList() {
        return myCalendarService.queryMyManageCalendarList();
    }

    // 同时获取我的日程和他人日程列表，供给分组 select2使用
    @Override
    public Select2GroupData queryCalendarListForSelect2Group(Select2QueryInfo queryInfo) {
        List<MyCalendarEntity> myList = this.queryAppMyCalendarList();
        Select2GroupData data = new Select2GroupData();
        if (CollectionUtils.isNotEmpty(myList)) {
            for (MyCalendarEntity entity : myList) {
                Select2DataBean bean = new Select2DataBean(entity.getUuid(), entity.getCalendarName());
                data.addResultData("我的日程", bean);
            }
        }
        List<MyCalendarEntity> otherList = this.queryMyManageCalendarList();
        if (CollectionUtils.isNotEmpty(otherList)) {
            for (MyCalendarEntity otherEntity : otherList) {
                Select2DataBean bean = new Select2DataBean(otherEntity.getUuid(), otherEntity.getCalendarName());
                data.addResultData("他人日程", bean);
            }
        }
        return data;
    }

    // 按授权行为获取对应的日历本列表
    @Override
    public Select2GroupData queryMyManagerCalendarListByActionForSelect2Group(Select2QueryInfo queryInfo) {
        String id = "";
        if (queryInfo.getParams() != null && queryInfo.getParams().get("id") != null) {
            id = queryInfo.getParams().get("id").toString();
        }
        Select2GroupData data = new Select2GroupData();

        // 查看状态，只需要返回自己就可以了
        if (StringUtils.isNotBlank(id)) {
            MyCalendarEntity calendar = myCalendarService.getOne(id);
            Select2DataBean bean = new Select2DataBean(calendar.getUuid(), calendar.getCalendarName());
            data.addResultData("我的日程", bean);
            return data;
        } else {
            List<MyCalendarEntity> myList = this.queryAppMyCalendarList();
            // 新增和编辑状态
            if (CollectionUtils.isNotEmpty(myList)) {
                for (MyCalendarEntity entity : myList) {
                    Select2DataBean bean = new Select2DataBean(entity.getUuid(), entity.getCalendarName());
                    data.addResultData("我的日程", bean);
                }
            }
            List<MyCalendarEntity> otherList = Lists.newArrayList();
            String selected = "";
            if (queryInfo.getParams() != null && queryInfo.getParams().get("selected") != null) {
                selected = queryInfo.getParams().get("selected").toString();
            }
            if (StringUtils.isBlank(selected)) { // 新增操作
                otherList = myCalendarService.queryMyAllowAddCalendarList();
            } else { // 编辑操作
                otherList = myCalendarService.queryMyAllowEditCalendarList();
            }
            if (CollectionUtils.isNotEmpty(otherList)) {
                for (MyCalendarEntity otherEntity : otherList) {
                    Select2DataBean bean = new Select2DataBean(otherEntity.getUuid(), otherEntity.getCalendarName());
                    data.addResultData("他人日程", bean);
                }
            }
            return data;
        }

    }

    @Override
    public void updateEventStatus(String uuid, Integer status) {
        myCalendarEventService.updateStatus(uuid, status);
    }

    // 根据repeatMarkId, 批量删除重复的事项
    @Override
    public Boolean deleteEventsByRepeatMarkId(String repeatMarkId) {
        myCalendarEventService.deleteEventByRepeatMarkId(repeatMarkId);
        return true;
    }

    @Override
    public Map<String, String> getGroupDataMap() {
        // 因为是按日历本的创建者进行的分组，所以查出所有的日历本对应的创建者账号
        // 获取我可以管理的日历本
        Map<String, String> map = new HashMap<String, String>();
        List<MyCalendarEntity> objs = this.myCalendarService.listAll();
        if (CollectionUtils.isNotEmpty(objs)) {
            for (MyCalendarEntity myCalendarEntity : objs) {
                String userName = orgApiFacade.getUserNameById(myCalendarEntity.getCreator());
                map.put(myCalendarEntity.getCreator(), userName);
            }
        }
        return map;
    }

    @Override
    public CalendarRights queryCalendarPrivilege(String calendarUuid) {
        return this.myCalendarService.queryCalendarPrivilege(calendarUuid);
    }

    @Override
    public List<Double> getFileSizesByIds(String fileIds) {
        return this.myCalendarService.getFileSizesByIds(fileIds);
    }

    @Override
    public ResultMessage judgeConflictZone(String startDate, String endDate, String dataUuid) {
        ResultMessage resultMessage = new ResultMessage();
        List<MyCalendarEventEntity> myCalendarEventEntitys = this.myCalendarEventService
                .queryAppMyCalendarEventListBySection(startDate, endDate);
        resultMessage.setSuccess(true);
        StringBuilder data = new StringBuilder();
        if (myCalendarEventEntitys != null && myCalendarEventEntitys.size() > 0) {

            for (int eventIndex = 0; eventIndex < myCalendarEventEntitys.size(); eventIndex++) {
                if (!myCalendarEventEntitys.get(eventIndex).getUuid().equals(dataUuid)) {
                    data.append(myCalendarEventEntitys.get(eventIndex).getTitle());
                    if (eventIndex != myCalendarEventEntitys.size() - 1) {
                        data.append("、");
                    }
                }
            }
            if (StringUtils.isNotBlank(data.toString())) {
                String dataStr = "该日程与#" + data.toString() + "#冲突,是否继续添加?";
                resultMessage.setData(dataStr);
            }
        }
        return resultMessage;
    }
}
