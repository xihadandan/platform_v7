/*
 * @(#)2021-08-25 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.jdbc.entity.IdEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * Description: 数据库表WF_FLOW_INSPECTION_FILE_RECORD的实体类
 *
 * @author zenghw
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021-08-25.1	zenghw		2021-08-25		Create
 * </pre>
 * @date 2021-08-25
 */
@Entity
@Table(name = "WF_FLOW_INSPECTION_FILE_RECORD")
@DynamicUpdate
@DynamicInsert
public class WfFlowInspectionFileRecordEntity extends IdEntity {

    private static final long serialVersionUID = 1629863247148L;

    // 签批日志（json字符串结构）
    private String inspectionLog;
    // 流程实例UUID
    private String flowInstUuid;
    // 原附件fileUuid
    private String fileUuid;
    // 手写签批文件uuid
    private String inspectionFileUuid;

    /**
     * @return the inspectionLog
     */
    public String getInspectionLog() {
        return this.inspectionLog;
    }

    /**
     * @param inspectionLog
     */
    public void setInspectionLog(String inspectionLog) {
        this.inspectionLog = inspectionLog;
    }

    /**
     * @return the flowInstUuid
     */
    public String getFlowInstUuid() {
        return this.flowInstUuid;
    }

    /**
     * @param flowInstUuid
     */
    public void setFlowInstUuid(String flowInstUuid) {
        this.flowInstUuid = flowInstUuid;
    }

    /**
     * @return the fileUuid
     */
    public String getFileUuid() {
        return this.fileUuid;
    }

    /**
     * @param fileUuid
     */
    public void setFileUuid(String fileUuid) {
        this.fileUuid = fileUuid;
    }

    /**
     * @return the inspectionFileUuid
     */
    public String getInspectionFileUuid() {
        return this.inspectionFileUuid;
    }

    /**
     * @param inspectionFileUuid
     */
    public void setInspectionFileUuid(String inspectionFileUuid) {
        this.inspectionFileUuid = inspectionFileUuid;
    }

}
