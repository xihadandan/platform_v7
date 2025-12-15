/*
 * @(#)2022-03-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.handover.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据库表WH_WORK_TYPE_TO_HANDOVER的实体类
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
@Table(name = "WH_WORK_TYPE_TO_HANDOVER")
@DynamicUpdate
@DynamicInsert
public class WhWorkTypeToHandoverEntity extends IdEntity {

    private static final long serialVersionUID = 1647939051951L;

    // 是否含已办结流程 0代表不含 1代表含
    private Integer completedFlowFlag;
    // 工作交接UUID
    private String whWorkHandoverUuid;
    // 交接内容类型 HandoverContentTypeEnum 待办流程:todo
    // ;查阅流程:consult;监控流程:monitor;已办流程:done;督办流程:supervise;
    private String handoverContentType;
    // 交接内容类型显示值
    private String handoverContentTypeName;

    public String getHandoverContentTypeName() {
        return this.handoverContentTypeName;
    }

    public void setHandoverContentTypeName(final String handoverContentTypeName) {
        this.handoverContentTypeName = handoverContentTypeName;
    }

    /**
     * @return the completedFlowFlag
     */
    public Integer getCompletedFlowFlag() {
        return this.completedFlowFlag;
    }

    /**
     * @param completedFlowFlag
     */
    public void setCompletedFlowFlag(Integer completedFlowFlag) {
        this.completedFlowFlag = completedFlowFlag;
    }

    /**
     * @return the whWorkHandoverUuid
     */
    public String getWhWorkHandoverUuid() {
        return this.whWorkHandoverUuid;
    }

    /**
     * @param whWorkHandoverUuid
     */
    public void setWhWorkHandoverUuid(String whWorkHandoverUuid) {
        this.whWorkHandoverUuid = whWorkHandoverUuid;
    }

    /**
     * @return the handoverContentType
     */
    public String getHandoverContentType() {
        return this.handoverContentType;
    }

    /**
     * @param handoverContentType
     */
    public void setHandoverContentType(String handoverContentType) {
        this.handoverContentType = handoverContentType;
    }

}
