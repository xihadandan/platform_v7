/**
 * Copyright (c) 2005-2012, www.dengwl.com
 * All rights reserved.
 *
 * @Title: ScheduleModifyRequest.java
 * @Package com.wellsoft.pt.api.request
 * @Description: TODO
 * @author Administrator
 * @date 2014-12-19 下午1:17:19
 * @version V1.0
 */
package com.wellsoft.pt.api.request;

import com.wellsoft.pt.api.WellptRequest;
import com.wellsoft.pt.api.internal.suport.ApiServiceName;
import com.wellsoft.pt.api.response.ScheduleModifyResponse;
import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * @author Administrator
 * @ClassName: ScheduleModifyRequest
 * @Description: TODO
 * @date 2014-12-19 下午1:17:19
 */
public class ScheduleModifyRequest extends WellptRequest<ScheduleModifyResponse> {
    /**
     * @Fields id : 日历id
     */
    private String id;
    private String name;//主题
    private String belongto;// 日历类别
    private String color;// 事件颜色
    private String startDate;// 开始日期
    private String startTime;// 开始时间
    private String endDate;// 结束日期
    private String endTime;// 结束时间
    private int repeat;// 重复方式(1:不重复、2:每日、3:每工作日、4:每周、5:每月、6:每年)
    private String address;// 地点
    private String sponsor;// 发起人
    private String invitees;// 邀请的人
    private int tip;// 提醒(1:不提醒、2:前五分钟、3:前十分钟、4:前半小时、5:前一小时、6:前两小时、7:指定时间)
    private String tipMethod;//提醒方式 (1:邮件、2:手机短信、3:消息、4:弹框)
    private String noticMethod;//提醒被邀请人的方式
    private String content;// 内容
    private String attach;// 附件
    private String tipTime;// 设置提醒时间
    private String tipDate;// 提醒日期

    public String getNoticMethod() {
        return noticMethod;
    }

    public void setNoticMethod(String noticMethod) {
        this.noticMethod = noticMethod;
    }

    /* (No Javadoc)
     * <p>Title: getApiServiceName</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getApiServiceName()
     */
    @Override
    public String getApiServiceName() {
        // TODO Auto-generated method stub
        return ApiServiceName.SCHEDULE_MODIFY;
    }

    /* (No Javadoc)
     * <p>Title: getResponseClass</p>
     * <p>Description: </p>
     * @return
     * @see com.wellsoft.pt.api.WellptRequest#getResponseClass()
     */
    @Override
    @JsonIgnore
    public Class<ScheduleModifyResponse> getResponseClass() {
        // TODO Auto-generated method stub
        return ScheduleModifyResponse.class;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the belongto
     */
    public String getBelongto() {
        return belongto;
    }

    /**
     * @param belongto the belongto to set
     */
    public void setBelongto(String belongto) {
        this.belongto = belongto;
    }

    /**
     * @return the color
     */
    public String getColor() {
        return color;
    }

    /**
     * @param color the color to set
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the startTime
     */
    public String getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the endTime
     */
    public String getEndTime() {
        return endTime;
    }

    /**
     * @param endTime the endTime to set
     */
    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    /**
     * @return the repeat
     */
    public int getRepeat() {
        return repeat;
    }

    /**
     * @param repeat the repeat to set
     */
    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    /**
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return the sponsor
     */
    public String getSponsor() {
        return sponsor;
    }

    /**
     * @param sponsor the sponsor to set
     */
    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    /**
     * @return the invitees
     */
    public String getInvitees() {
        return invitees;
    }

    /**
     * @param invitees the invitees to set
     */
    public void setInvitees(String invitees) {
        this.invitees = invitees;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * @param content the content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * @return the attach
     */
    public String getAttach() {
        return attach;
    }

    /**
     * @param attach the attach to set
     */
    public void setAttach(String attach) {
        this.attach = attach;
    }

    /**
     * @return the tip
     */
    public int getTip() {
        return tip;
    }

    /**
     * @param tip the tip to set
     */
    public void setTip(int tip) {
        this.tip = tip;
    }

    public String getTipMethod() {
        return tipMethod;
    }

    public void setTipMethod(String tipMethod) {
        this.tipMethod = tipMethod;
    }

    public String getTipTime() {
        return tipTime;
    }

    public void setTipTime(String tipTime) {
        this.tipTime = tipTime;
    }

    public String getTipDate() {
        return tipDate;
    }

    public void setTipDate(String tipDate) {
        this.tipDate = tipDate;
    }

}
