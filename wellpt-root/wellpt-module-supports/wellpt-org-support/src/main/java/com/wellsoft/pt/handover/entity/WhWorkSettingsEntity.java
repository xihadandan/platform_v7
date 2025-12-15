/*
 * @(#)2022-03-22 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.handover.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Description: 数据库表WH_WORK_SETTINGS的实体类
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
@Table(name = "WH_WORK_SETTINGS")
@DynamicUpdate
@DynamicInsert
public class WhWorkSettingsEntity extends TenantEntity {

    private static final long serialVersionUID = 1647939052627L;

    // 默认执行时间 格式 ：01:00
    private String workTime;

    /**
     * @return the workTime
     */
    public String getWorkTime() {
        return this.workTime;
    }

    /**
     * @param workTime
     */
    public void setWorkTime(String workTime) {
        this.workTime = workTime;
    }

}
