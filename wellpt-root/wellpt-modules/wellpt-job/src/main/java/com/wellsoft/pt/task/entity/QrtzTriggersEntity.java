/*
 * @(#)2018年11月13日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.task.entity;

import com.wellsoft.context.jdbc.entity.CommonEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年11月13日.1	zhulh		2018年11月13日		Create
 * </pre>
 * @date 2018年11月13日
 */
@Entity
@Table(name = "QRTZ_TRIGGERS")
@DynamicUpdate
@DynamicInsert
@CommonEntity
public class QrtzTriggersEntity implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8998989151198322006L;

    private String entryId;

    private String triggerName;

    private String triggerGroup;

    private String triggerState;

    /**
     * @return the entryId
     */
    @Id
    public String getEntryId() {
        return entryId;
    }

    /**
     * @param entryId 要设置的entryId
     */
    public void setEntryId(String entryId) {
        this.entryId = entryId;
    }

    /**
     * @return the triggerName
     */
    public String getTriggerName() {
        return triggerName;
    }

    /**
     * @param triggerName 要设置的triggerName
     */
    public void setTriggerName(String triggerName) {
        this.triggerName = triggerName;
    }

    /**
     * @return the triggerGroup
     */
    public String getTriggerGroup() {
        return triggerGroup;
    }

    /**
     * @param triggerGroup 要设置的triggerGroup
     */
    public void setTriggerGroup(String triggerGroup) {
        this.triggerGroup = triggerGroup;
    }

    /**
     * @return the triggerState
     */
    public String getTriggerState() {
        return triggerState;
    }

    /**
     * @param triggerState 要设置的triggerState
     */
    public void setTriggerState(String triggerState) {
        this.triggerState = triggerState;
    }

}
