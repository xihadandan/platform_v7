package com.wellsoft.pt.workflow.entity;

import com.wellsoft.context.jdbc.entity.TenantEntity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Date;

/**
 * Description: 流程定义删除日志
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021年7月14日.1	zhulh		2021年7月14日		Create
 * </pre>
 * @date 2021年7月14日
 */
@Entity
@Table(name = "WF_FLOW_DEFINITION_DELETE_LOG")
@DynamicUpdate
@DynamicInsert
public class WfFlowDefinitionDeleteLogEntity extends TenantEntity {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8492123547000988305L;

    // 流程定义UUID
    private String flowDefUuid;

    // 流程定义名称
    private String flowDefName;

    // 流程定义ID
    private String flowDefId;

    // 流程版本
    private Double flowDefVersion;

    // 流程分类UUID
    private String categoryUuid;

    // 流程分类名称
    private String categoryName;

    // 对应表单UUID
    private String formUuid;

    // 对应表单名称
    private String formName;

    // 删除时间
    private Date deleteTime;

    // 删除类型1逻辑删除、2物理删除
    private Integer deleteType;

    /**
     * @return the flowDefUuid
     */
    public String getFlowDefUuid() {
        return flowDefUuid;
    }

    /**
     * @param flowDefUuid 要设置的flowDefUuid
     */
    public void setFlowDefUuid(String flowDefUuid) {
        this.flowDefUuid = flowDefUuid;
    }

    /**
     * @return the flowDefName
     */
    public String getFlowDefName() {
        return flowDefName;
    }

    /**
     * @param flowDefName 要设置的flowDefName
     */
    public void setFlowDefName(String flowDefName) {
        this.flowDefName = flowDefName;
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
     * @return the flowDefVersion
     */
    public Double getFlowDefVersion() {
        return flowDefVersion;
    }

    /**
     * @param flowDefVersion 要设置的flowDefVersion
     */
    public void setFlowDefVersion(Double flowDefVersion) {
        this.flowDefVersion = flowDefVersion;
    }

    /**
     * @return the categoryUuid
     */
    public String getCategoryUuid() {
        return categoryUuid;
    }

    /**
     * @param categoryUuid 要设置的categoryUuid
     */
    public void setCategoryUuid(String categoryUuid) {
        this.categoryUuid = categoryUuid;
    }

    /**
     * @return the categoryName
     */
    public String getCategoryName() {
        return categoryName;
    }

    /**
     * @param categoryName 要设置的categoryName
     */
    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
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
     * @return the formName
     */
    public String getFormName() {
        return formName;
    }

    /**
     * @param formName 要设置的formName
     */
    public void setFormName(String formName) {
        this.formName = formName;
    }

    /**
     * @return the deleteTime
     */
    public Date getDeleteTime() {
        return deleteTime;
    }

    /**
     * @param deleteTime 要设置的deleteTime
     */
    public void setDeleteTime(Date deleteTime) {
        this.deleteTime = deleteTime;
    }

    /**
     * @return the deleteType
     */
    public Integer getDeleteType() {
        return deleteType;
    }

    /**
     * @param deleteType 要设置的deleteType
     */
    public void setDeleteType(Integer deleteType) {
        this.deleteType = deleteType;
    }

}
