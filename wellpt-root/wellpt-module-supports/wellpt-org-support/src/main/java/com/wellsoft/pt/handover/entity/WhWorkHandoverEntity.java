/*
 * @(#)2022-03-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.handover.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import io.swagger.annotations.ApiModelProperty;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 数据库表WH_WORK_HANDOVER的实体类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2022-03-22.1	zenghw		2022-03-22		Create
 * </pre>
 * @date 2022-03-22
 */
@Entity
@Table(name = "WH_WORK_HANDOVER")
@DynamicUpdate
@DynamicInsert
public class WhWorkHandoverEntity extends TenantEntity {

    private static final long serialVersionUID = 1647939051489L;

    // 交接人ID-单选
    @ApiModelProperty("交接人ID")
    private String handoverPersonId;
    // 任务实际开始执行的时间
    @ApiModelProperty("任务实际开始执行的时间")
    private Date handoverWorkTime;
    // 是否通知接收人-NoticeHandoverPersonFlagEnum 0:不通知；1通知
    @ApiModelProperty("是否通知接收人")
    private Integer noticeHandoverPersonFlag;
    // 交接内容：流程定义内容显示值
    @ApiModelProperty("交接内容：流程定义内容显示值")
    private String handoverContentsName;
    // 交接内容：流程定义内容
    @ApiModelProperty("交接内容：流程定义内容")
    private String handoverContentsId;
    // 接收人ID-单选
    @ApiModelProperty("接收人ID")
    private String receiverId;
    // 交接人名称-单选
    @ApiModelProperty("交接人名称")
    private String handoverPersonName;
    // 工作交接状态-WorkHandoverStatusEnum 1未执行；2执行中；3已完成；
    @ApiModelProperty("工作交接状态")
    private Integer workHandoverStatus;
    // 接收人名称-单选
    @ApiModelProperty("接收人名称")
    private String receiverName;
    // 交接执行时间-HandoverworktimesettingEnum 1系统空闲时执行 ;2立即执行
    @ApiModelProperty("交接执行时间")
    private Integer handoverWorkTimeSetting;
    // 工作类型-HandoverWorkTypeEnum 流程： flow;
    @ApiModelProperty("工作类型")
    private String handoverWorkType;

    @ApiModelProperty("工作类型显示值")
    private String handoverWorkTypeName;

    public String getHandoverWorkTypeName() {
        return this.handoverWorkTypeName;
    }

    public void setHandoverWorkTypeName(final String handoverWorkTypeName) {
        this.handoverWorkTypeName = handoverWorkTypeName;
    }

    /**
     * @return the handoverPersonId
     */
    public String getHandoverPersonId() {
        return this.handoverPersonId;
    }

    /**
     * @param handoverPersonId
     */
    public void setHandoverPersonId(String handoverPersonId) {
        this.handoverPersonId = handoverPersonId;
    }

    /**
     * @return the handoverWorkTime
     */
    public Date getHandoverWorkTime() {
        return this.handoverWorkTime;
    }

    /**
     * @param handoverWorkTime
     */
    public void setHandoverWorkTime(Date handoverWorkTime) {
        this.handoverWorkTime = handoverWorkTime;
    }

    /**
     * @return the noticeHandoverPersonFlag
     */
    public Integer getNoticeHandoverPersonFlag() {
        return this.noticeHandoverPersonFlag;
    }

    /**
     * @param noticeHandoverPersonFlag
     */
    public void setNoticeHandoverPersonFlag(Integer noticeHandoverPersonFlag) {
        this.noticeHandoverPersonFlag = noticeHandoverPersonFlag;
    }

    /**
     * @return the handoverContentsName
     */
    public String getHandoverContentsName() {
        return this.handoverContentsName;
    }

    /**
     * @param handoverContentsName
     */
    public void setHandoverContentsName(String handoverContentsName) {
        this.handoverContentsName = handoverContentsName;
    }

    /**
     * @return the handoverContentsId
     */
    public String getHandoverContentsId() {
        return this.handoverContentsId;
    }

    /**
     * @param handoverContentsId
     */
    public void setHandoverContentsId(String handoverContentsId) {
        this.handoverContentsId = handoverContentsId;
    }

    /**
     * @return the receiverId
     */
    public String getReceiverId() {
        return this.receiverId;
    }

    /**
     * @param receiverId
     */
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    /**
     * @return the handoverPersonName
     */
    public String getHandoverPersonName() {
        return this.handoverPersonName;
    }

    /**
     * @param handoverPersonName
     */
    public void setHandoverPersonName(String handoverPersonName) {
        this.handoverPersonName = handoverPersonName;
    }

    /**
     * @return the workHandoverStatus
     */
    public Integer getWorkHandoverStatus() {
        return this.workHandoverStatus;
    }

    /**
     * @param workHandoverStatus
     */
    public void setWorkHandoverStatus(Integer workHandoverStatus) {
        this.workHandoverStatus = workHandoverStatus;
    }

    /**
     * @return the receiverName
     */
    public String getReceiverName() {
        return this.receiverName;
    }

    /**
     * @param receiverName
     */
    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    /**
     * @return the handoverWorkTimeSetting
     */
    public Integer getHandoverWorkTimeSetting() {
        return this.handoverWorkTimeSetting;
    }

    /**
     * @param handoverWorkTimeSetting
     */
    public void setHandoverWorkTimeSetting(Integer handoverWorkTimeSetting) {
        this.handoverWorkTimeSetting = handoverWorkTimeSetting;
    }

    /**
     * @return the handoverWorkType
     */
    public String getHandoverWorkType() {
        return this.handoverWorkType;
    }

    /**
     * @param handoverWorkType
     */
    public void setHandoverWorkType(String handoverWorkType) {
        this.handoverWorkType = handoverWorkType;
    }

}
