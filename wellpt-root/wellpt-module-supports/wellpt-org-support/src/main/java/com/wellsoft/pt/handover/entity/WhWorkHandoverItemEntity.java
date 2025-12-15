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
 * Description: 数据库表WH_WORK_HANDOVER_ITEM的实体类
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
@Table(name = "WH_WORK_HANDOVER_ITEM")
@DynamicUpdate
@DynamicInsert
public class WhWorkHandoverItemEntity extends IdEntity {

    private static final long serialVersionUID = 1647939052226L;

    // 工作类型是流程的话，值为taskInstUuid
    private String dataUuid;

    private String whWorkHandoverUuid;
    // HandoverItemStatusEnum 0待交接；1交接成功；2交接失败
    private Integer handoverItemStatus;
    // HandoverItemTypeEnum 待办：TODO；已办：DONE;监控：MONITOR;督办：SUPERVISE；查阅：CONSULT

    private String handoverItemType;

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return this.dataUuid;
    }

    /**
     * @param dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
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
     * @return the handoverItemStatus
     */
    public Integer getHandoverItemStatus() {
        return this.handoverItemStatus;
    }

    /**
     * @param handoverItemStatus
     */
    public void setHandoverItemStatus(Integer handoverItemStatus) {
        this.handoverItemStatus = handoverItemStatus;
    }

    /**
     * @return the handoverItemType
     */
    public String getHandoverItemType() {
        return this.handoverItemType;
    }

    /**
     * @param handoverItemType
     */
    public void setHandoverItemType(String handoverItemType) {
        this.handoverItemType = handoverItemType;
    }

}
