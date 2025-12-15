/*
 * @(#)2016-06-03 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.calendar.entity;

import com.wellsoft.pt.app.calendar.bean.RemindConf;
import com.wellsoft.pt.app.calendar.bean.RepeatConf;
import com.wellsoft.pt.basicdata.viewcomponent.facade.support.calendar.CalendarEventEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.Date;

/**
 * Description: 日程事件
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
@Entity
@Table(name = "APP_MY_CALENDAR_EVENT")
@DynamicUpdate
@DynamicInsert
public class MyCalendarEventEntity extends CalendarEventEntity {

    public static final String FIELD_NAME_STATUS = "isFinish";

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1673871163474743423L;
    public static String PUBLIC_RANGE_ALL = "4"; // 所有人可见
    public static String PUBLIC_RANGE_PARTUSER = "3"; // 部分用户
    public static String PUBLIC_RANGE_LEADER = "2"; // 部分用户
    public static String PUBLIC_RANGE_JOINUSER = "1"; // 部分用户
    private String address; // 地点
    private String fileUuids; // 附件
    private String joinUsers; // 参与人
    private String publicRange; // 公开范围
    private String noticeTypes; // 通知方式
    private String noticeObjs; // 通知对象
    private String repeatMarkId; // 重复的标识ID, 如果一样，则说明这些事项是通过重复批量建立的
    private Integer isAll; // 是否全天
    private Integer isRemind; // 是否提醒
    private Integer isRepeat; // 是否重复
    @Transient
    private RepeatConf repeatConfVo; // 重复的配置
    private String repeatConf;
    @Transient
    private RemindConf remindConfVo; // 提醒的配置
    private String remindConf; // 提醒的配置
    private Integer isFinish; // 是否已完成
    private Date repeatPeriodStartTime; // 重复的周期开始时间， 如果没有重复，该值=事项的开始时间
    private Date repeatPeriodEndTime; // 重复的周期结束时间
    private String calendarCreator; // 日历本创建者
    private String calendarCreatorName; // 日历本创建者名称
    private Boolean isCanView = true; // 是否可查看
    private Date remindTime; // 提醒时间
    private Integer remindStatus;// 提醒状态, 0:为提醒，1已提醒
    private String partUsers; // 部分用户可见，当publicRange=部分人员时，该配置项生效

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address 要设置的address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the fileUuids
     */
    public String getFileUuids() {
        return fileUuids;
    }

    /**
     * @param fileUuids 要设置的fileUuids
     */
    public void setFileUuids(String fileUuids) {
        this.fileUuids = fileUuids;
    }

    /**
     * @return the joinUsers
     */
    public String getJoinUsers() {
        return joinUsers;
    }

    /**
     * @param joinUsers 要设置的joinUsers
     */
    public void setJoinUsers(String joinUsers) {
        this.joinUsers = joinUsers;
    }

    /**
     * @return the publicRange
     */
    public String getPublicRange() {
        return publicRange;
    }

    /**
     * @param publicRange 要设置的publicRange
     */
    public void setPublicRange(String publicRange) {
        this.publicRange = publicRange;
    }

    /**
     * @return the noticeTypes
     */
    public String getNoticeTypes() {
        return noticeTypes;
    }

    /**
     * @param noticeTypes 要设置的noticeTypes
     */
    public void setNoticeTypes(String noticeTypes) {
        this.noticeTypes = noticeTypes;
    }

    /**
     * @return the noticeObjs
     */
    public String getNoticeObjs() {
        return noticeObjs;
    }

    /**
     * @param noticeObjs 要设置的noticeObjs
     */
    public void setNoticeObjs(String noticeObjs) {
        this.noticeObjs = noticeObjs;
    }

    /**
     * @return the repeatMarkId
     */
    public String getRepeatMarkId() {
        return repeatMarkId;
    }

    /**
     * @param repeatMarkId 要设置的repeatMarkId
     */
    public void setRepeatMarkId(String repeatMarkId) {
        this.repeatMarkId = repeatMarkId;
    }

    /**
     * @return the isAll
     */
    public Integer getIsAll() {
        return isAll;
    }

    /**
     * @param isAll 要设置的isAll
     */
    public void setIsAll(Integer isAll) {
        this.isAll = isAll;
    }

    /**
     * @return the isRemind
     */
    public Integer getIsRemind() {
        return isRemind;
    }

    /**
     * @param isRemind 要设置的isRemind
     */
    public void setIsRemind(Integer isRemind) {
        this.isRemind = isRemind;
    }

    /**
     * @return the isRepeat
     */
    public Integer getIsRepeat() {
        return isRepeat;
    }

    /**
     * @param isRepeat 要设置的isRepeat
     */
    public void setIsRepeat(Integer isRepeat) {
        this.isRepeat = isRepeat;
    }

    /**
     * @return the isFinish
     */
    public Integer getIsFinish() {
        return isFinish;
    }

    /**
     * @param isFinish 要设置的isFinish
     */
    public void setIsFinish(Integer isFinish) {
        this.isFinish = isFinish;
    }

    /**
     * @return the repeatPeriodStartTime
     */
    public Date getRepeatPeriodStartTime() {
        return repeatPeriodStartTime;
    }

    /**
     * @param repeatPeriodStartTime 要设置的repeatPeriodStartTime
     */
    public void setRepeatPeriodStartTime(Date repeatPeriodStartTime) {
        this.repeatPeriodStartTime = repeatPeriodStartTime;
    }

    /**
     * @return the repeatPeriodEndTime
     */
    public Date getRepeatPeriodEndTime() {
        return repeatPeriodEndTime;
    }

    /**
     * @param repeatPeriodEndTime 要设置的repeatPeriodEndTime
     */
    public void setRepeatPeriodEndTime(Date repeatPeriodEndTime) {
        this.repeatPeriodEndTime = repeatPeriodEndTime;
    }

    /**
     * @return the remindConf
     */
    public String getRemindConf() {
        return remindConf;
    }

    /**
     * @param remindConf 要设置的remindConf
     */
    public void setRemindConf(String remindConf) {
        this.remindConf = remindConf;
    }

    /**
     * @return the calendarCreator
     */
    public String getCalendarCreator() {
        return calendarCreator;
    }

    /**
     * @param calendarCreator 要设置的calendarCreator
     */
    public void setCalendarCreator(String calendarCreator) {
        this.calendarCreator = calendarCreator;
    }

    /**
     * @return the isCanView
     */
    @Transient
    public Boolean getIsCanView() {
        return isCanView;
    }

    /**
     * @param isCanView 要设置的isCanView
     */
    public void setIsCanView(Boolean isCanView) {
        this.isCanView = isCanView;
    }

    /**
     * @return the remindTime
     */
    public Date getRemindTime() {
        return remindTime;
    }

    /**
     * @param remindTime 要设置的remindTime
     */
    public void setRemindTime(Date remindTime) {
        this.remindTime = remindTime;
    }

    /**
     * @return the remindStatus
     */
    public Integer getRemindStatus() {
        return remindStatus;
    }

    /**
     * @param remindStatus 要设置的remindStatus
     */
    public void setRemindStatus(Integer remindStatus) {
        this.remindStatus = remindStatus;
    }

    /**
     * @return the partUsers
     */
    public String getPartUsers() {
        return partUsers;
    }

    /**
     * @param partUsers 要设置的partUsers
     */
    public void setPartUsers(String partUsers) {
        this.partUsers = partUsers;
    }

    /**
     * @return the calendarCreatorName
     */
    public String getCalendarCreatorName() {
        return calendarCreatorName;
    }

    /**
     * @param calendarCreatorName 要设置的calendarCreatorName
     */
    public void setCalendarCreatorName(String calendarCreatorName) {
        this.calendarCreatorName = calendarCreatorName;
    }

    /**
     * @return the repeatConfVo
     */
    @Transient
    public RepeatConf getRepeatConfVo() {
        return repeatConfVo;
    }

    /**
     * @param repeatConfVo 要设置的repeatConfVo
     */
    public void setRepeatConfVo(RepeatConf repeatConfVo) {
        this.repeatConfVo = repeatConfVo;
    }

    /**
     * @return the repeatConf
     */
    public String getRepeatConf() {
        return repeatConf;
    }

    /**
     * @param repeatConf 要设置的repeatConf
     */
    public void setRepeatConf(String repeatConf) {
        this.repeatConf = repeatConf;
    }

    /**
     * @return the remindConfVo
     */
    @Transient
    public RemindConf getRemindConfVo() {
        return remindConfVo;
    }

    /**
     * @param remindConfVo 要设置的remindConfVo
     */
    public void setRemindConfVo(RemindConf remindConfVo) {
        this.remindConfVo = remindConfVo;
    }

}
