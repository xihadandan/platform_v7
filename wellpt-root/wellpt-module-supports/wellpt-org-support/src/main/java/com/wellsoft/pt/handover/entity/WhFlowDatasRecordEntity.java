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
 * Description: 数据库表WH_FLOW_DATAS_RECORD的实体类
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
@Table(name = "WH_FLOW_DATAS_RECORD")
@DynamicUpdate
@DynamicInsert
public class WhFlowDatasRecordEntity extends IdEntity {

    private static final long serialVersionUID = 1647939052432L;

    private Integer todoCount;

    private Integer consultCount;

    private Integer monitorCount;

    private String whWorkHandoverUuid;

    private Integer doneCount;

    private Integer superviseCount;

    /**
     * @return the todoCount
     */
    public Integer getTodoCount() {
        return this.todoCount;
    }

    /**
     * @param todoCount
     */
    public void setTodoCount(Integer todoCount) {
        this.todoCount = todoCount;
    }

    /**
     * @return the consultCount
     */
    public Integer getConsultCount() {
        return this.consultCount;
    }

    /**
     * @param consultCount
     */
    public void setConsultCount(Integer consultCount) {
        this.consultCount = consultCount;
    }

    /**
     * @return the monitorCount
     */
    public Integer getMonitorCount() {
        return this.monitorCount;
    }

    /**
     * @param monitorCount
     */
    public void setMonitorCount(Integer monitorCount) {
        this.monitorCount = monitorCount;
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
     * @return the doneCount
     */
    public Integer getDoneCount() {
        return this.doneCount;
    }

    /**
     * @param doneCount
     */
    public void setDoneCount(Integer doneCount) {
        this.doneCount = doneCount;
    }

    /**
     * @return the superviseCount
     */
    public Integer getSuperviseCount() {
        return this.superviseCount;
    }

    /**
     * @param superviseCount
     */
    public void setSuperviseCount(Integer superviseCount) {
        this.superviseCount = superviseCount;
    }

}
