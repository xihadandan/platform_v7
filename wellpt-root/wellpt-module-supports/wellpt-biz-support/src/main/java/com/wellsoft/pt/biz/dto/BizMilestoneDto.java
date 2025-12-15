/*
 * @(#)11/18/22 V1.0
 *
 * Copyright 2022 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.biz.dto;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import io.swagger.annotations.ApiModel;
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
 * 11/18/22.1	zhulh		11/18/22		Create
 * </pre>
 * @date 11/18/22
 */
@ApiModel("里程碑事件数据传输类")
public class BizMilestoneDto extends BaseObject {
    private static final long serialVersionUID = 1238562407735133714L;

    @ApiModelProperty("里程碑事件名称")
    private String name;

    @ApiModelProperty("事项实例UUID")
    private String itemInstUuid;

    @ApiModelProperty("里程碑事件描述")
    private String remark;

    @ApiModelProperty("表单数据")
    private DyFormData dyFormData;

    @ApiModelProperty("交付物字段列表")
    private List<String> resultFields;

    /**
     * @param name
     * @param itemInstUuid
     * @param remark
     * @param dyFormData
     * @param resultFields
     */
    public BizMilestoneDto(String name, String itemInstUuid, String remark, DyFormData dyFormData, List<String> resultFields) {
        this.name = name;
        this.itemInstUuid = itemInstUuid;
        this.remark = remark;
        this.dyFormData = dyFormData;
        this.resultFields = resultFields;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

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
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * @return the dyFormData
     */
    public DyFormData getDyFormData() {
        return dyFormData;
    }

    /**
     * @param dyFormData 要设置的dyFormData
     */
    public void setDyFormData(DyFormData dyFormData) {
        this.dyFormData = dyFormData;
    }

    /**
     * @return the resultFields
     */
    public List<String> getResultFields() {
        return resultFields;
    }

    /**
     * @param resultFields 要设置的resultFields
     */
    public void setResultFields(List<String> resultFields) {
        this.resultFields = resultFields;
    }
}
