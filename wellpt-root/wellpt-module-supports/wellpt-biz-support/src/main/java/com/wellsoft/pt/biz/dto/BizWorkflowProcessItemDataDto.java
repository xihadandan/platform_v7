/*
 * @(#)6/19/24 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.dto;

import com.wellsoft.pt.bpm.engine.form.CustomDynamicButton;
import com.wellsoft.pt.repository.entity.LogicFileInfo;
import com.wellsoft.pt.workflow.work.bean.WorkBean;
import io.swagger.annotations.ApiModelProperty;

import java.util.List;


/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 6/19/24.1	zhulh		6/19/24		Create
 * </pre>
 * @date 6/19/24
 */
public class BizWorkflowProcessItemDataDto extends BizProcessItemDataDto {

    private static final long serialVersionUID = -1640796687247888061L;

    private WorkBean workData;

    /**
     * @return
     */
    public WorkBean getWorkData() {
        return workData;
    }

    /**
     * @param workData
     */
    public void setWorkData(WorkBean workData) {
        this.workData = workData;
    }

}
