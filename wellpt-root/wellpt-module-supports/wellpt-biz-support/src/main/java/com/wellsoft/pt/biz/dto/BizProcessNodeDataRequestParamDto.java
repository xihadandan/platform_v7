/*
 * @(#)10/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.dto;

import com.wellsoft.context.base.BaseObject;
import io.swagger.annotations.ApiModelProperty;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 10/18/22.1	zhulh		10/18/22		Create
 * </pre>
 * @date 10/18/22
 */
public class BizProcessNodeDataRequestParamDto extends BaseObject {
    private static final long serialVersionUID = -6762634974126321190L;

    @ApiModelProperty("过程节点实例UUID")
    private String processNodeInstUuid;

    @ApiModelProperty("表单定义UUID")
    private String formUuid;

    @ApiModelProperty("表单定义UUID")
    private String dataUuid;

    /**
     * @return the processNodeInstUuid
     */
    public String getProcessNodeInstUuid() {
        return processNodeInstUuid;
    }

    /**
     * @param processNodeInstUuid 要设置的processNodeInstUuid
     */
    public void setProcessNodeInstUuid(String processNodeInstUuid) {
        this.processNodeInstUuid = processNodeInstUuid;
    }

    /**
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * @return the dataUuid
     */
    public String getDataUuid() {
        return dataUuid;
    }

    /**
     * @param dataUuid 要设置的dataUuid
     */
    public void setDataUuid(String dataUuid) {
        this.dataUuid = dataUuid;
    }
}
