/*
 * @(#)2018年6月8日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.app.workflow.dto;

import com.wellsoft.context.base.BaseObject;
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
 * 2018年6月8日.1	zhulh		2018年6月8日		Create
 * </pre>
 * @date 2018年6月8日
 */
@ApiModel("送审批内容")
public class ApproveContentDto extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -1555758385780661452L;

    // 发送内容类型
    @ApiModelProperty("送审批类型")
    private String type;
    // 表单定义UUID
    @ApiModelProperty("表单定义UUID")
    private String formUuid;
    // 表单数据UUID
    @ApiModelProperty("表单数据UUID")
    private String dataUuid;
    // 单据转换规则
    @ApiModelProperty("单据转换规则")
    private String botRuleId;
    // 只填充可编辑的字段
    @ApiModelProperty("只填充可编辑的字段")
    private boolean onlyFillEditableField = false;
    // 流程定义ID
    @ApiModelProperty("流程定义ID")
    private String flowDefId;
    //    // 链接标题
//    @ApiModelProperty("链接标题")
//    private String linkTitle;
//    // 链接URL
//    @ApiModelProperty("链接URL")
//    private String linkUrl;
    @ApiModelProperty("链接信息")
    private List<ApproveLinkInfoDto> links;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
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

    /**
     * @return the botRuleId
     */
    public String getBotRuleId() {
        return botRuleId;
    }

    /**
     * @param botRuleId 要设置的botRuleId
     */
    public void setBotRuleId(String botRuleId) {
        this.botRuleId = botRuleId;
    }

    /**
     * @return the onlyFillEditableField
     */
    public boolean isOnlyFillEditableField() {
        return onlyFillEditableField;
    }

    /**
     * @param onlyFillEditableField 要设置的onlyFillEditableField
     */
    public void setOnlyFillEditableField(boolean onlyFillEditableField) {
        this.onlyFillEditableField = onlyFillEditableField;
    }

    /**
     * @return the flowDefId
     */
    public String getFlowDefId() {
        return flowDefId;
    }

    /**
     * @param flowDefId 要设置的flowDefId
     */
    public void setFlowDefId(String flowDefId) {
        this.flowDefId = flowDefId;
    }

    /**
     * @return the links
     */
    public List<ApproveLinkInfoDto> getLinks() {
        return links;
    }

    /**
     * @param links 要设置的links
     */
    public void setLinks(List<ApproveLinkInfoDto> links) {
        this.links = links;
    }
}
