/*
 * @(#)Mar 29, 2018 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.query.api;

import com.wellsoft.context.jdbc.support.BaseQueryItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 流程定义查询结果VO类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * Mar 29, 2018.1	zhulh		Mar 29, 2018		Create
 * </pre>
 * @date Mar 29, 2018
 */
@ApiModel("流程定义")
@Table(name = "wf_flow_definition")
public class FlowDefinitionQueryItem implements BaseQueryItem {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -4774966267360627626L;

    // UUID
    @ApiModelProperty("定义UUID")
    private String uuid;

    // 创建时间
    @ApiModelProperty("创建时间")
    private Date createTime;

    // 分类
    @ApiModelProperty("分类UUID")
    private String category;

    // 分类名称
    @ApiModelProperty("分类名称")
    private String categoryName;

    // 名称
    @ApiModelProperty("名称")
    private String name;

    // 别名
    @ApiModelProperty("ID")
    private String id;

    // 编号
    @ApiModelProperty("编号")
    private String code;

    // 版本
    @ApiModelProperty("版本")
    private Double version;

    // 对应表单UUID
    @ApiModelProperty("对应表单UUID")
    private String formUuid;

    // 对应表单名称
    @ApiModelProperty("对应表单名称")
    private String formName;

    // 是否启用
    @ApiModelProperty("是否启用")
    private Boolean enabled;

    // 是否移动端显示
    @ApiModelProperty("是否PC端显示")
    private Boolean pcShowFlag;

    // 是否移动端显示
    @ApiModelProperty("是否移动端显示")
    private Boolean isMobileShow;

    // 是否是自由流程
    @ApiModelProperty("是否是自由流程")
    private Boolean freeed;

    // 等价流程定义ID
    @ApiModelProperty("等价流程定义ID")
    private String equalFlowId;

    // 流程备注
    @ApiModelProperty("备注")
    private String remark;

    // 归属系统单位ID
    @ApiModelProperty("归属系统单位ID")
    private String systemUnitId;

    @ApiModelProperty("删除时间")
    private Date deleteTime;

    // 删除状态0正常，1已逻辑删除，2已逻辑删除且不可彻底删除
    @ApiModelProperty("删除状态")
    private Integer deleteStatus;

    public Boolean getPcShowFlag() {
        return this.pcShowFlag;
    }

    public void setPcShowFlag(final Boolean pcShowFlag) {
        this.pcShowFlag = pcShowFlag;
    }

    /**
     * 获取定义UUID
     *
     * @return the uuid
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * 设置定义UUID
     *
     * @param uuid 要设置的uuid
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * 获取创建时间
     *
     * @return the createTime
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * 设置创建时间
     *
     * @param createTime 要设置的createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    /**
     * 获取分类UUID
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置分类UUID
     *
     * @param category 要设置的category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * 获取分类名称
     *
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * 设置分类名称
     *
     * @param categoryName 要设置的categoryName
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    /**
     * 获取名称
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * 设置名称
     *
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 获取ID
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置ID
     *
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * 获取编号
     *
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * 设置编号
     *
     * @param code 要设置的code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 获取版本
     *
     * @return the version
     */
    public Double getVersion() {
        return version;
    }

    /**
     * 设置版本
     *
     * @param version 要设置的version
     */
    public void setVersion(Double version) {
        this.version = version;
    }

    /**
     * 获取对应表单UUID
     *
     * @return the formUuid
     */
    public String getFormUuid() {
        return formUuid;
    }

    /**
     * 设置对应表单UUID
     *
     * @param formUuid 要设置的formUuid
     */
    public void setFormUuid(String formUuid) {
        this.formUuid = formUuid;
    }

    /**
     * 获取对应表单名称
     *
     * @return the formName
     */
    public String getFormName() {
        return formName;
    }

    /**
     * 设置对应表单名称
     *
     * @param formName 要设置的formName
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }

    /**
     * 获取是否启用
     *
     * @return the enabled
     */
    public Boolean getEnabled() {
        return enabled;
    }

    /**
     * 设置是否启用
     *
     * @param enabled 要设置的enabled
     */
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * 获取是否移动端显示
     *
     * @return the isMobileShow
     */
    public Boolean getIsMobileShow() {
        return isMobileShow;
    }

    /**
     * 设置是否移动端显示
     *
     * @param isMobileShow 要设置的isMobileShow
     */
    public void setIsMobileShow(Boolean isMobileShow) {
        this.isMobileShow = isMobileShow;
    }

    /**
     * 获取是否是自由流程
     *
     * @return the freeed
     */
    public Boolean getFreeed() {
        return freeed;
    }

    /**
     * 设置是否是自由流程
     *
     * @param freeed 要设置的freeed
     */
    public void setFreeed(Boolean freeed) {
        this.freeed = freeed;
    }

    /**
     * 获取等价流程定义ID
     *
     * @return the equalFlowId
     */
    public String getEqualFlowId() {
        return equalFlowId;
    }

    /**
     * 设置等价流程定义ID
     *
     * @param equalFlowId 要设置的equalFlowId
     */
    public void setEqualFlowId(String equalFlowId) {
        this.equalFlowId = equalFlowId;
    }

    /**
     * 获取备注
     *
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置备注
     *
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取归属系统单位ID
     *
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * 设置归属系统单位ID
     *
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }


    public Date getDeleteTime() {
        return deleteTime;
    }

    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }
}
