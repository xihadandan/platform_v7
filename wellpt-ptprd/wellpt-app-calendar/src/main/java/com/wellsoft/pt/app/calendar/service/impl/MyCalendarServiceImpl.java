/*
 * @(#)2018年3月1日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.service.impl;

import com.google.common.base.Function;
import com.google.common.collect.Lists;
import com.google.common.collect.Ordering;
import com.wellsoft.context.web.controller.ResultMessage;
import com.wellsoft.pt.app.calendar.bean.CalendarRights;
import com.wellsoft.pt.app.calendar.dao.MyCalendarDao;
import com.wellsoft.pt.app.calendar.entity.MyCalendarEntity;
import com.wellsoft.pt.app.calendar.service.MyCalendarService;
import com.wellsoft.pt.common.generator.service.IdGeneratorService;
import com.wellsoft.pt.jpa.service.impl.AbstractJpaServiceImpl;
import com.wellsoft.pt.jpa.util.BeanUtils;
import com.wellsoft.pt.multi.org.facade.service.OrgApiFacade;
import com.wellsoft.pt.repository.entity.mongo.file.MongoFileEntity;
import com.wellsoft.pt.repository.service.MongoFileService;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Description: 如何描述该类
 *
 * @author chenqiong
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年3月1日.1	chenqiong		2018年3月1日		Create
 * </pre>
 * @date 2018年3月1日
 */
@Service
public class MyCalendarServiceImpl extends AbstractJpaServiceImpl<MyCalendarEntity, MyCalendarDao, String> implements
        MyCalendarService {

    @Autowired
    private OrgApiFacade orgApiFacade;
    @Autowired
    private MongoFileService mongoFileService;
    @Autowired
    private IdGeneratorService idGeneratorService;

    private static boolean isNotBlank(String userId) {
        return StringUtils.isNotBlank(userId) && false == "null".equals(userId) && false == "undefined".equals(userId);
    }

    // 获取我的日历本列表
    @Override
    public List<MyCalendarEntity> queryAppMyCalendarList() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        MyCalendarEntity q = new MyCalendarEntity();
        q.setCreator(userId);
        // 因为is_default是通过表单建立的字段，虽然设置了默认值，但是数据库中并不会真的产生默认值，所以这里需要做一次is_default的排序
        List<MyCalendarEntity> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isNotEmpty(objs)) {
            Ordering<MyCalendarEntity> ordering = Ordering.natural().onResultOf(
                    new Function<MyCalendarEntity, Integer>() {
                        @Override
                        public Integer apply(MyCalendarEntity arg0) {
                            return arg0.getIsDefault() == null ? 0 : arg0.getIsDefault();
                        }
                    });
            return ordering.sortedCopy(objs);
        } else {
            return Lists.newArrayList();
        }
    }

    // 我管理的他们的日历本
    @Override
    public List<MyCalendarEntity> queryMyManageCalendarList() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<MyCalendarEntity> otherUserList = this.dao.listByFieldNotEqValue("creator", userId);
        List<MyCalendarEntity> list = Lists.newArrayList();
        if (otherUserList != null) {
            for (MyCalendarEntity myCalendarEntity : otherUserList) {
                // 判断是否有查看日历本的权限
                if (isHasViewCalendarPrivilege(userId, myCalendarEntity)) {
                    String creatorId = myCalendarEntity.getCreator();
                    String creatorName = orgApiFacade.getUserNameById(creatorId);
                    if (StringUtils.isNotBlank(creatorName) && creatorName != null) {//脏数据会取不到名称
                        myCalendarEntity.setCalendarName(myCalendarEntity.getCalendarName() + "(" + creatorName + ")");
                    } else {
                        myCalendarEntity.setCalendarName(myCalendarEntity.getCalendarName());
                    }
                    list.add(myCalendarEntity);
                }
            }
        }
        return list;
    }

    private boolean isHasViewCalendarPrivilege(String userId, MyCalendarEntity calendar) {
        // 判断用户是否在授予权限的范围人员之内，如果是，就可以看到这个日历本,如果不是，则不能看到这个日历本
        if (MyCalendarServiceImpl.isNotBlank(calendar.getManagersValue())) {
            HashMap<String, String> userMap = orgApiFacade.getUsersByOrgIds(calendar.getManagersValue());
            return userMap.containsKey(userId);
        }
        return false;
    }

    @Override
    public CalendarRights queryCalendarPrivilege(String calendarUuid) {
        MyCalendarEntity calendar = this.getOne(calendarUuid);
        if (calendar == null) {
            throw new RuntimeException("找不到对应的日历本，或该日历本已被删除");
        }
        String userId = SpringSecurityUtils.getCurrentUserId();

        CalendarRights rights = new CalendarRights();
        // 自己的日历本，具备所有权限
        if (calendar.getCreator().equals(userId)) {
            rights.setIsAdd(true);
            rights.setIsDel(true);
            rights.setIsEdit(true);
            rights.setIsStatus(true);
            return rights;
        }
        if (MyCalendarServiceImpl.isNotBlank(calendar.getManagersValue())) {
            // 判断用户是否在授权的范围内
            HashMap<String, String> userMap = orgApiFacade.getUsersByOrgIds(calendar.getManagersValue());
            if (userMap.containsKey(userId)) {
                if (StringUtils.isNotBlank(calendar.getRightRangeValue())) {
                    String[] ranges = calendar.getRightRangeValue().split(";");
                    // add;edit;del;status
                    for (String r : ranges) {
                        if ("add".equals(r)) {
                            rights.setIsAdd(true);
                        } else if ("edit".equals(r)) {
                            rights.setIsEdit(true);
                        } else if ("del".equals(r)) {
                            rights.setIsDel(true);
                        } else if ("status".equals(r)) {
                            rights.setIsStatus(true);
                        }
                    }
                }
            }
        }
        return rights;
    }

    // 允许我添加的日历本列表
    @Override
    public List<MyCalendarEntity> queryMyAllowAddCalendarList() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<MyCalendarEntity> otherUserList = this.dao.listByFieldNotEqValue("creator", userId);
        List<MyCalendarEntity> list = Lists.newArrayList();
        if (otherUserList != null) {
            for (MyCalendarEntity myCalendarEntity : otherUserList) {
                // 判断是否有添加日历本的权限
                if (isHasAddCalendarPrivilege(userId, myCalendarEntity)) {
                    String creatorId = myCalendarEntity.getCreator();
                    String creatorName = orgApiFacade.getUserNameById(creatorId);
                    myCalendarEntity.setCalendarName(myCalendarEntity.getCalendarName() + "(" + creatorName + ")");
                    list.add(myCalendarEntity);
                }
            }
        }
        return list;
    }

    private boolean isHasAddCalendarPrivilege(String userId, MyCalendarEntity calendar) {
        // 判断用户是否在授予权限的范围人员之内，如果是，则检查授予的权限里面有没有添加的权限
        if (MyCalendarServiceImpl.isNotBlank(calendar.getManagersValue())) {
            HashMap<String, String> userMap = orgApiFacade.getUsersByOrgIds(calendar.getManagersValue());
            if (userMap.containsKey(userId)) {
                if (calendar.getRightRange() != null && calendar.getRightRange().indexOf("add") > -1) {
                    return true;
                }
            }
        }
        return false;
    }

    // 允许我编辑的日历本列表
    @Override
    public List<MyCalendarEntity> queryMyAllowEditCalendarList() {
        String userId = SpringSecurityUtils.getCurrentUserId();
        List<MyCalendarEntity> otherUserList = this.dao.listByFieldNotEqValue("creator", userId);
        List<MyCalendarEntity> list = Lists.newArrayList();
        if (otherUserList != null) {
            for (MyCalendarEntity myCalendarEntity : otherUserList) {
                // 判断是否有添加日历本的权限
                if (isHasEditCalendarPrivilege(userId, myCalendarEntity)) {
                    String creatorId = myCalendarEntity.getCreator();
                    String creatorName = orgApiFacade.getUserNameById(creatorId);
                    myCalendarEntity.setCalendarName(myCalendarEntity.getCalendarName() + "(" + creatorName + ")");
                    list.add(myCalendarEntity);
                }
            }
        }
        return list;
    }

    private boolean isHasEditCalendarPrivilege(String userId, MyCalendarEntity calendar) {
        // 判断用户是否在授予权限的范围人员之内，如果是，则检查授予的权限里面有没有添加的权限
        if (MyCalendarServiceImpl.isNotBlank(calendar.getManagersValue())) {
            HashMap<String, String> userMap = orgApiFacade.getUsersByOrgIds(calendar.getManagersValue());
            if (userMap.containsKey(userId)) {
                if (calendar.getRightRange() != null && calendar.getRightRange().indexOf("edit") > -1) {
                    return true;
                }
            }
        }
        return false;
    }

    // 是否有查看事项的权限
    @Override
    public boolean isHasViewEventPrivilege(String userId, String calendarUuid) {
        MyCalendarEntity calendar = this.getOne(calendarUuid);
        // 自己的日历本
        if (StringUtils.equals(calendar.getCreator(), userId)) {
            return true;
        }
        // 有授予管理权限，则能查看
        Map<String, String> userMap = new HashMap<String, String>();
        if (MyCalendarServiceImpl.isNotBlank(calendar.getManagersValue())) {
            userMap.putAll(orgApiFacade.getUsersByOrgIds(calendar.getManagersValue()));
        }
        if (userMap.containsKey(userId)) {
            return true;
        } else {
            // 公开范围
            String pr = calendar.getPublicRangeValue();
            if (StringUtils.isBlank(pr)) {
                return false;
            } else if (MyCalendarEntity.PUBLIC_RANGE_NO.equals(pr)) {
                return false;
            } else if (MyCalendarEntity.PUBLIC_RANGE_ALL.equals(pr)) {
                return true;
            } else if (MyCalendarServiceImpl.isNotBlank(calendar.getPartUsersValue())) {
                HashMap<String, String> partUserMap = orgApiFacade.getUsersByOrgIds(calendar.getPartUsersValue());
                return partUserMap.containsKey(userId);
            }
            return false;
        }
    }

    // 获取默认的日历本
    @Override
    public MyCalendarEntity getDefaultCalendar(String userId) {
        MyCalendarEntity q = new MyCalendarEntity();
        q.setIsDefault(1);
        q.setCreator(userId);
        List<MyCalendarEntity> objs = this.dao.listByEntity(q);
        if (CollectionUtils.isEmpty(objs)) {
            return null;
        }
        return objs.get(0);
    }

    /**
     * 通过文件的uuid获取到文件的大小
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.app.calendar.service.MyCalendarService#getFileSizesByIds(java.lang.String)
     */
    @Override
    public List<Double> getFileSizesByIds(String fileIds) {
        ResultMessage msg = new ResultMessage();
        List<Double> longs = new ArrayList<Double>();
        String[] files = fileIds.split(";");
        for (String file : files) {
            MongoFileEntity fileEntity = mongoFileService.getFile(file);
            longs.add(fileEntity.getLength() / (double) 1024 / (double) 1024D);
        }
        return longs;
    }

    @Override
    @Transactional
    public void saveBean(MyCalendarEntity bean) {
        MyCalendarEntity category = new MyCalendarEntity();
        if (StringUtils.isNotBlank(bean.getUuid())) {
            category = this.dao.getOne(bean.getUuid());
        } else {
            // 编号唯一性判断
            // category.setCode(bean.getCode());
            // if (CollectionUtils.isNotEmpty(this.dao.listByEntity(category))) {
            // throw new RuntimeException("已经存在编号为[" + category.getCode() + "]的流程分类!");
            // }
        }

        BeanUtils.copyPropertiesExcludeBaseField(bean, category, new String[]{"systemUnitId"});
        this.dao.save(category);
    }

    @Override
    public MyCalendarEntity getBean(String uuid) {
        MyCalendarEntity bean = new MyCalendarEntity();
        MyCalendarEntity category = dao.getOne(uuid);
        BeanUtils.copyProperties(category, bean);
        return bean;
    }

    @Override
    @Transactional
    public String generateCalendarCategoryCode() {
        return idGeneratorService.generate(MyCalendarEntity.class, "00000", false);
    }

    @Override
    @Transactional
    public void removeAllByPk(Collection<String> uids) {
        for (String uid : uids) {
            delete(uid);
        }
    }

}
