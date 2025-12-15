/*
 * @(#)2012-10-23 V1.0
 *
 * Copyright 2012 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.entity;

import com.wellsoft.context.annotation.UnCloneable;
import com.wellsoft.context.jdbc.entity.IdEntity;
import com.wellsoft.context.validator.MaxLength;
import io.swagger.annotations.ApiModelProperty;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.validator.constraints.NotBlank;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.Set;

/**
 * Description: 流程定义实体类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本		修改人		修改日期			修改内容
 * 2012-10-23.1	zhulh		2012-10-23		Create
 * </pre>
 * @date 2012-10-23
 */
@Entity
@Table(name = "WF_FLOW_DEFINITION")
@DynamicUpdate
@DynamicInsert
public class FlowDefinition extends IdEntity {

    private static final long serialVersionUID = -527036172257793243L;

    /**
     * 分类
     */
    private String category;

    /**
     * 名称
     */
    @NotBlank
    @MaxLength(max = 255)
    private String name;

    /**
     * 别名
     */
    @NotBlank
    @MaxLength(max = 255)
    private String id;

    /**
     * 编号
     */
    @MaxLength(max = 255)
    private String code;

    /**
     * 版本
     */
    @NotNull
    @com.wellsoft.context.validator.Number
    private Double version;

    /**
     * 对应表单UUID
     */
    private String formUuid;

    /**
     * 对应表单名称
     */
    private String formName;

    /**
     * 是否启用 流程状态
     */
    private Boolean enabled;

    /**
     * 是否是自由流程
     */
    private Boolean freeed;

    /**
     * 等价流程定义ID
     */
    private String equalFlowId;

    /**
     * 二次开发配置JSON信息
     */
    private String developJson;

    /**
     * 流程备注
     */
    private String remark;

    // 多职流转设置
    private String multiJobFlowType;

    // 职位字段
    private String jobField;

    /**
     * 流程规划
     */
    private String flowSchemaUuid;

    /**
     * 流程定义的流程实例
     **/
    @UnCloneable
    private Set<FlowInstance> flowInstances;

    /* add by huanglinchuan2014.10.20 begin */
    // 流程标题表达式
    private String titleExpression;

    // 归属系统ID
    private String systemUnitId;

    private String moduleId;

    // 移动端展示
    private Boolean isMobileShow;

    // PC端展示
    private Boolean pcShowFlag;

    /**
     * 别名
     */
    @MaxLength(max = 255)
    private String applyId;
    // 是否自动更新标题
    private Boolean autoUpdateTitle;
    // 删除时间
    private Date deleteTime;

    // 删除状态0正常，1已逻辑删除，2已逻辑删除且不可彻底删除
    private Integer deleteStatus;
    /* add by huanglinchuan2014.10.20 end */

    @ApiModelProperty("归属系统")
    protected String system;
    @ApiModelProperty("归属租户")
    protected String tenant;

    /**
     * 获取分类
     *
     * @return the category
     */
    public String getCategory() {
        return category;
    }

    /**
     * 设置分类
     *
     * @param category 要设置的category
     */
    public void setCategory(String category) {
        this.category = category;
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
     * 获取别名
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * 设置别名
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
     * 获取二次开发配置JSON信息
     *
     * @return the developJson
     */
    public String getDevelopJson() {
        return developJson;
    }

    /**
     * 设置二次开发配置JSON信息
     *
     * @param developJson 要设置的developJson
     */
    public void setDevelopJson(String developJson) {
        this.developJson = developJson;
    }

    /**
     * @return the flowSchemaUuid
     */
    public String getFlowSchemaUuid() {
        return flowSchemaUuid;
    }

    /**
     * @param flowSchemaUuid 要设置的flowSchemaUuid
     */
    public void setFlowSchemaUuid(String flowSchemaUuid) {
        this.flowSchemaUuid = flowSchemaUuid;
    }

    /**
     * 获取流程实例
     *
     * @retuFormDefinitionRefEntityrn the flowInstances
     */
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "flowDefinition")
    @Cascade({CascadeType.ALL})
    @OrderBy("startTime desc")
    @JsonIgnore
    public Set<FlowInstance> getFlowInstances() {
        return flowInstances;
    }

    /**
     * 设置流程实例
     *
     * @param flowInstances 要设置的flowInstances
     */
    public void setFlowInstances(Set<FlowInstance> flowInstances) {
        this.flowInstances = flowInstances;
    }

    /**
     * 获取流程备注
     *
     * @return the remark
     */
    public String getRemark() {
        return remark;
    }

    /**
     * 设置流程备注
     *
     * @param remark 要设置的remark
     */
    public void setRemark(String remark) {
        this.remark = remark;
    }

    /**
     * 获取归属系统ID
     *
     * @return the systemUnitId
     */
    public String getSystemUnitId() {
        return systemUnitId;
    }

    /**
     * 设置归属系统ID
     *
     * @param systemUnitId 要设置的systemUnitId
     */
    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    /**
     * 获取应用于ID
     *
     * @return
     */
    public String getApplyId() {
        return applyId;
    }

    /**
     * 设置应用于ID
     *
     * @param applyId
     */
    public void setApplyId(String applyId) {
        this.applyId = applyId;
    }

    /**
     * 获取归属模块ID
     *
     * @return
     */
    public String getModuleId() {
        return moduleId;
    }

    /**
     * 设置归属模块ID
     *
     * @param moduleId
     */
    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    /**
     * 获取移动端展示
     *
     * @return
     */
    public Boolean getIsMobileShow() {
        return isMobileShow;
    }

    /**
     * 设置移动端展示
     *
     * @param mobileShow
     */
    public void setIsMobileShow(Boolean mobileShow) {
        isMobileShow = mobileShow;
    }

    /**
     * @return
     */
    public Boolean getPcShowFlag() {
        return this.pcShowFlag;
    }

    /**
     * @param pcShowFlag
     */
    public void setPcShowFlag(final Boolean pcShowFlag) {
        this.pcShowFlag = pcShowFlag;
    }

    /**
     * 获取多职流转设置
     *
     * @return
     */
    public String getMultiJobFlowType() {
        return multiJobFlowType;
    }

    /**
     * 设置多职流转设置
     *
     * @param multiJobFlowType
     */
    public void setMultiJobFlowType(String multiJobFlowType) {
        this.multiJobFlowType = multiJobFlowType;
    }

    /**
     * 获取职位字段
     *
     * @return
     */
    public String getJobField() {
        return jobField;
    }

    /**
     * 设置职位字段
     *
     * @param jobField
     */
    public void setJobField(String jobField) {
        this.jobField = jobField;
    }

    /**
     * 获取是否自动更新标题
     *
     * @return
     */
    public Boolean getAutoUpdateTitle() {
        return autoUpdateTitle;
    }

    /**
     * 设置是否自动更新标题
     *
     * @param autoUpdateTitle
     */
    public void setAutoUpdateTitle(Boolean autoUpdateTitle) {
        this.autoUpdateTitle = autoUpdateTitle;
    }

    /**
     * 获取流程标题表达式
     *
     * @return
     */
    public String getTitleExpression() {
        return titleExpression;
    }

    /**
     * 设置流程标题表达式
     *
     * @param titleExpression
     */
    public void setTitleExpression(String titleExpression) {
        this.titleExpression = titleExpression;
    }

    /**
     * 获取删除时间
     *
     * @return the deleteTime
     */
    public Date getDeleteTime() {
        return deleteTime;
    }

    /**
     * 设置删除时间
     *
     * @param deleteTime 要设置的deleteTime
     */
    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    /**
     * 获取删除状态0正常，1已逻辑删除，2已逻辑删除且不可彻底删除
     *
     * @return the deleteStatus
     */
    public Integer getDeleteStatus() {
        return deleteStatus;
    }

    /**
     * 设置删除状态0正常，1已逻辑删除，2已逻辑删除且不可彻底删除
     *
     * @param deleteStatus 要设置的deleteStatus
     */
    public void setDeleteStatus(Integer deleteStatus) {
        this.deleteStatus = deleteStatus;
    }

    /**
     * @return the system
     */
    public String getSystem() {
        return system;
    }

    /**
     * @param system 要设置的system
     */
    public void setSystem(String system) {
        this.system = system;
    }

    /**
     * @return the tenant
     */
    public String getTenant() {
        return tenant;
    }

    /**
     * @param tenant 要设置的tenant
     */
    public void setTenant(String tenant) {
        this.tenant = tenant;
    }
}
