/*
 * @(#)10/13/22 V1.0
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
 * 10/13/22.1	zhulh		10/13/22		Create
 * </pre>
 * @date 10/13/22
 */
public class BizProcessItemDataRequestParamDto extends BaseObject {
    private static final long serialVersionUID = -3011900573815271849L;

    @ApiModelProperty("事项实例UUID")
    private String itemInstUuid;

    @ApiModelProperty("上级事项实例UUID")
    private String parentItemInstUuid;

    @ApiModelProperty("事项流定义ID")
    private String itemFlowId;

    @ApiModelProperty("事项流实例UUID")
    private String itemFlowInstUuid;

    @ApiModelProperty("业务流程定义ID")
    private String processDefId;

    @ApiModelProperty("事项ID")
    private String itemId;

    @ApiModelProperty("表单定义UUID")
    private String formUuid;

    @ApiModelProperty("表单定义UUID")
    private String dataUuid;

    /**
     * @return the itemInstUuid
     */
    public String getItemInstUuid() {
        return itemInstUuid;
    }

    /**
     * @param itemInstUuid 要设置的itemInstUuid
     */
    public void setItemInstUuid(String itemInstUuid) {
        this.itemInstUuid = itemInstUuid;
    }

    /**
     * @return the parentItemInstUuid
     */
    public String getParentItemInstUuid() {
        return parentItemInstUuid;
    }

    /**
     * @param parentItemInstUuid 要设置的parentItemInstUuid
     */
    public void setParentItemInstUuid(String parentItemInstUuid) {
        this.parentItemInstUuid = parentItemInstUuid;
    }

    /**
     * @return the itemFlowId
     */
    public String getItemFlowId() {
        return itemFlowId;
    }

    /**
     * @param itemFlowId 要设置的itemFlowId
     */
    public void setItemFlowId(String itemFlowId) {
        this.itemFlowId = itemFlowId;
    }

    /**
     * @return the itemFlowInstUuid
     */
    public String getItemFlowInstUuid() {
        return itemFlowInstUuid;
    }

    /**
     * @param itemFlowInstUuid 要设置的itemFlowInstUuid
     */
    public void setItemFlowInstUuid(String itemFlowInstUuid) {
        this.itemFlowInstUuid = itemFlowInstUuid;
    }

    /**
     * @return the processDefId
     */
    public String getProcessDefId() {
        return processDefId;
    }

    /**
     * @param processDefId 要设置的processDefId
     */
    public void setProcessDefId(String processDefId) {
        this.processDefId = processDefId;
    }

    /**
     * @return the itemId
     */
    public String getItemId() {
        return itemId;
    }

    /**
     * @param itemId 要设置的itemId
     */
    public void setItemId(String itemId) {
        this.itemId = itemId;
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
