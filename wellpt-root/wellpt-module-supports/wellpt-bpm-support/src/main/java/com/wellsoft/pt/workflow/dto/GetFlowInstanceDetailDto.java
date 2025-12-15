package com.wellsoft.pt.workflow.dto;

import com.wellsoft.context.jdbc.entity.IdEntity;

import java.util.Date;

/**
 * Description:
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	        修改人		修改日期			修改内容
 * 2021/9/2.1	    zenghw		2021/9/2		    Create
 * </pre>
 * @date 2021/9/2
 */
public class GetFlowInstanceDetailDto extends IdEntity {

    /**
     * 流程定义id
     */
    private String id;

    /**
     * 流程实例名称
     */
    private String name;

    /**
     * 动态表单定义UUID
     */
    private String formUuid;

    /**
     * 动态表单数据UUID
     */
    private String dataUuid;

    /**
     * 标题
     */
    private String title;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 是否计时
     */
    private Boolean isTiming;

    /**
     * 计时状态0正常、1预警、2到期、3逾期
     */
    private Integer timingState;

    /**
     * 是否逾期
     */
    private Boolean isOverDue;

    /**
     * 预警时间
     */
    private Date alarmTime;

    /**
     * 到期时间、逾期时间、承诺时间
     */
    private Date dueTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 流程经历的总时间，以毫秒为单位
     */
    private Long duration;

    /**
     * 流程启动者
     */
    private String startUserId;

    /**
     * 流程实例所有者ID
     */
    private String ownerId;

    /**
     * 流程发起部门ID
     */
    private String startDepartmentId;

    private String startJobId;

    /**
     * 流程所属部门ID
     */
    private String ownerDepartmentId;

    /**
     * 流程发起单位ID
     */
    private String startUnitId;

    /**
     * 流程所属单位ID
     */
    private String ownerUnitId;

    /**
     * 当前流程是否处理活动状态
     */
    private Boolean isActive;

    /**
     * 归属系统ID
     */
    private String systemUnitId;

    /**
     * 预留字段
     **/
    // 255字符长度
    private String reservedText1;
    // 255字符长度
    private String reservedText2;
    // 255字符长度
    private String reservedText3;
    // 255字符长度
    private String reservedText4;
    // 255字符长度
    private String reservedText5;
    // 255字符长度
    private String reservedText6;
    // 255字符长度
    private String reservedText7;
    // 255字符长度
    private String reservedText8;
    // 255字符长度
    private String reservedText9;
    // 255字符长度
    private String reservedText10;
    // 255字符长度
    private String reservedText11;
    // 255字符长度
    private String reservedText12;

    private Integer reservedNumber1;
    private Double reservedNumber2;
    private Double reservedNumber3;

    private Date reservedDate1;
    private Date reservedDate2;

    public String getId() {
        return this.id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public String getFormUuid() {
        return this.formUuid;
    }

    public void setFormUuid(final String formUuid) {
        this.formUuid = formUuid;
    }

    public String getDataUuid() {
        return this.dataUuid;
    }

    public void setDataUuid(final String dataUuid) {
        this.dataUuid = dataUuid;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Date getStartTime() {
        return this.startTime;
    }

    public void setStartTime(final Date startTime) {
        this.startTime = startTime;
    }

    public Boolean getTiming() {
        return this.isTiming;
    }

    public void setTiming(final Boolean timing) {
        this.isTiming = timing;
    }

    public Integer getTimingState() {
        return this.timingState;
    }

    public void setTimingState(final Integer timingState) {
        this.timingState = timingState;
    }

    public Boolean getOverDue() {
        return this.isOverDue;
    }

    public void setOverDue(final Boolean overDue) {
        this.isOverDue = overDue;
    }

    public Date getAlarmTime() {
        return this.alarmTime;
    }

    public void setAlarmTime(final Date alarmTime) {
        this.alarmTime = alarmTime;
    }

    public Date getDueTime() {
        return this.dueTime;
    }

    public void setDueTime(final Date dueTime) {
        this.dueTime = dueTime;
    }

    public Date getEndTime() {
        return this.endTime;
    }

    public void setEndTime(final Date endTime) {
        this.endTime = endTime;
    }

    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(final Long duration) {
        this.duration = duration;
    }

    public String getStartUserId() {
        return this.startUserId;
    }

    public void setStartUserId(final String startUserId) {
        this.startUserId = startUserId;
    }

    public String getOwnerId() {
        return this.ownerId;
    }

    public void setOwnerId(final String ownerId) {
        this.ownerId = ownerId;
    }

    public String getStartDepartmentId() {
        return this.startDepartmentId;
    }

    public void setStartDepartmentId(final String startDepartmentId) {
        this.startDepartmentId = startDepartmentId;
    }

    public String getStartJobId() {
        return this.startJobId;
    }

    public void setStartJobId(final String startJobId) {
        this.startJobId = startJobId;
    }

    public String getOwnerDepartmentId() {
        return this.ownerDepartmentId;
    }

    public void setOwnerDepartmentId(final String ownerDepartmentId) {
        this.ownerDepartmentId = ownerDepartmentId;
    }

    public String getStartUnitId() {
        return this.startUnitId;
    }

    public void setStartUnitId(final String startUnitId) {
        this.startUnitId = startUnitId;
    }

    public String getOwnerUnitId() {
        return this.ownerUnitId;
    }

    public void setOwnerUnitId(final String ownerUnitId) {
        this.ownerUnitId = ownerUnitId;
    }

    public Boolean getActive() {
        return this.isActive;
    }

    public void setActive(final Boolean active) {
        this.isActive = active;
    }

    public String getSystemUnitId() {
        return this.systemUnitId;
    }

    public void setSystemUnitId(final String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public String getReservedText1() {
        return this.reservedText1;
    }

    public void setReservedText1(final String reservedText1) {
        this.reservedText1 = reservedText1;
    }

    public String getReservedText2() {
        return this.reservedText2;
    }

    public void setReservedText2(final String reservedText2) {
        this.reservedText2 = reservedText2;
    }

    public String getReservedText3() {
        return this.reservedText3;
    }

    public void setReservedText3(final String reservedText3) {
        this.reservedText3 = reservedText3;
    }

    public String getReservedText4() {
        return this.reservedText4;
    }

    public void setReservedText4(final String reservedText4) {
        this.reservedText4 = reservedText4;
    }

    public String getReservedText5() {
        return this.reservedText5;
    }

    public void setReservedText5(final String reservedText5) {
        this.reservedText5 = reservedText5;
    }

    public String getReservedText6() {
        return this.reservedText6;
    }

    public void setReservedText6(final String reservedText6) {
        this.reservedText6 = reservedText6;
    }

    public String getReservedText7() {
        return this.reservedText7;
    }

    public void setReservedText7(final String reservedText7) {
        this.reservedText7 = reservedText7;
    }

    public String getReservedText8() {
        return this.reservedText8;
    }

    public void setReservedText8(final String reservedText8) {
        this.reservedText8 = reservedText8;
    }

    public String getReservedText9() {
        return this.reservedText9;
    }

    public void setReservedText9(final String reservedText9) {
        this.reservedText9 = reservedText9;
    }

    public String getReservedText10() {
        return this.reservedText10;
    }

    public void setReservedText10(final String reservedText10) {
        this.reservedText10 = reservedText10;
    }

    public String getReservedText11() {
        return this.reservedText11;
    }

    public void setReservedText11(final String reservedText11) {
        this.reservedText11 = reservedText11;
    }

    public String getReservedText12() {
        return this.reservedText12;
    }

    public void setReservedText12(final String reservedText12) {
        this.reservedText12 = reservedText12;
    }

    public Integer getReservedNumber1() {
        return this.reservedNumber1;
    }

    public void setReservedNumber1(final Integer reservedNumber1) {
        this.reservedNumber1 = reservedNumber1;
    }

    public Double getReservedNumber2() {
        return this.reservedNumber2;
    }

    public void setReservedNumber2(final Double reservedNumber2) {
        this.reservedNumber2 = reservedNumber2;
    }

    public Double getReservedNumber3() {
        return this.reservedNumber3;
    }

    public void setReservedNumber3(final Double reservedNumber3) {
        this.reservedNumber3 = reservedNumber3;
    }

    public Date getReservedDate1() {
        return this.reservedDate1;
    }

    public void setReservedDate1(final Date reservedDate1) {
        this.reservedDate1 = reservedDate1;
    }

    public Date getReservedDate2() {
        return this.reservedDate2;
    }

    public void setReservedDate2(final Date reservedDate2) {
        this.reservedDate2 = reservedDate2;
    }
}
