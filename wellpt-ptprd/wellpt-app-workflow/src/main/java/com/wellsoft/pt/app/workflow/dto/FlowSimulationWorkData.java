/*
 * @(#)10/15/24 V1.0
 *
 * Copyright 2024 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.dto;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.workflow.dto.WfFlowSimulationRecordDto;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description:
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 10/15/24.1	    zhulh		10/15/24		    Create
 * </pre>
 * @date 10/15/24
 */
@ApiModel("流程仿真工作数据")
public class FlowSimulationWorkData extends BaseObject {
    private static final long serialVersionUID = -8420101702500631045L;

    @ApiModelProperty("仿真记录UUID")
    private Long recordUuid;

    @ApiModelProperty("流程记录")
    private WfFlowSimulationRecordDto record;

    @ApiModelProperty("流程数据")
    private WorkBean workData;

    /**
     * @return the recordUuid
     */
    public Long getRecordUuid() {
        return recordUuid;
    }

    /**
     * @param recordUuid 要设置的recordUuid
     */
    public void setRecordUuid(Long recordUuid) {
        this.recordUuid = recordUuid;
    }

    /**
     * @return the record
     */
    public WfFlowSimulationRecordDto getRecord() {
        return record;
    }

    /**
     * @param record 要设置的record
     */
    public void setRecord(WfFlowSimulationRecordDto record) {
        this.record = record;
    }

    /**
     * @return the workData
     */
    public WorkBean getWorkData() {
        return workData;
    }

    /**
     * @param workData 要设置的workData
     */
    public void setWorkData(WorkBean workData) {
        this.workData = workData;
    }

}
