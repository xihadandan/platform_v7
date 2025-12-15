package com.wellsoft.pt.api.domain;

/**
 * Description: 日程实体（外部数据格式）
 *
 * @author lumw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-3-4.1	Lumw		2015-3-4		Create
 * </pre>
 * @date 2015-3-4
 */
public class Schedule {
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
    private String content;// 内容
    private String attach;// 附件

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBelongto() {
        return belongto;
    }

    public void setBelongto(String belongto) {
        this.belongto = belongto;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public int getRepeat() {
        return repeat;
    }

    public void setRepeat(int repeat) {
        this.repeat = repeat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSponsor() {
        return sponsor;
    }

    public void setSponsor(String sponsor) {
        this.sponsor = sponsor;
    }

    public String getInvitees() {
        return invitees;
    }

    public void setInvitees(String invitees) {
        this.invitees = invitees;
    }

    public int getTip() {
        return tip;
    }

    public void setTip(int tip) {
        this.tip = tip;
    }

    public String getTipMethod() {
        return tipMethod;
    }

    public void setTipMethod(String tipMethod) {
        this.tipMethod = tipMethod;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAttach() {
        return attach;
    }

    public void setAttach(String attach) {
        this.attach = attach;
    }
}
