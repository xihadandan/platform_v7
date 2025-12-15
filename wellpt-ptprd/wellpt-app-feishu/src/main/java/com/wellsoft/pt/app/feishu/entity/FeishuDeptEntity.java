package com.wellsoft.pt.app.feishu.entity;

import com.wellsoft.context.jdbc.entity.Entity;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import javax.persistence.Table;

/**
 * 飞书部门信息实体类
 */
@javax.persistence.Entity
@Table(name = "feishu_dept")
@DynamicUpdate
@DynamicInsert
public class FeishuDeptEntity extends Entity {
    /**
     * 飞书配置信息Uuid
     */
    private Long configUuid;

    /**
     * 行政组织UUID
     */
    private Long orgUuid;

    /**
     * 行政组织版本UUID
     */
    private Long orgVersionUuid;

    /**
     * 飞书应用的App ID
     */
    private String appId;

    /**
     * OA系统节点UUID
     */
    private Long orgElementUuid;

    /**
     * OA系统节点ID
     */
    private String orgElementId;

    /**
     * 部门名称
     */
    private String name;

    /**
     * 父部门的ID，在根部门下创建新部门，该参数值为 "0"
     */
    private String parentDepartmentId;

    /**
     * 本部门的自定义部门ID，注意：除需要满足正则规则外，同时不能以 `od-` 开头
     */
    private String departmentId;

    /**
     * 部门的open_id，类型与通过请求的查询参数传入的department_id_type相同
     */
    private String openDepartmentId;

    /**
     * 部门主管用户ID
     */
    private String leaderUserId;

    /**
     * 部门的排序，即部门在其同级部门的展示顺序
     */
    private String departmentOrder;

    /**
     * 部门状态
     */
    private String status;

    /**
     * 部门负责人
     */
    private String leaders;

    /**
     * @return the configUuid
     */
    public Long getConfigUuid() {
        return configUuid;
    }

    /**
     * @param configUuid 要设置的configUuid
     */
    public void setConfigUuid(Long configUuid) {
        this.configUuid = configUuid;
    }

    public Long getOrgUuid() {
        return orgUuid;
    }

    public void setOrgUuid(Long orgUuid) {
        this.orgUuid = orgUuid;
    }

    public Long getOrgVersionUuid() {
        return orgVersionUuid;
    }

    public void setOrgVersionUuid(Long orgVersionUuid) {
        this.orgVersionUuid = orgVersionUuid;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public Long getOrgElementUuid() {
        return orgElementUuid;
    }

    public void setOrgElementUuid(Long orgElementUuid) {
        this.orgElementUuid = orgElementUuid;
    }

    public String getOrgElementId() {
        return orgElementId;
    }

    public void setOrgElementId(String orgElementId) {
        this.orgElementId = orgElementId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentDepartmentId() {
        return parentDepartmentId;
    }

    public void setParentDepartmentId(String parentDepartmentId) {
        this.parentDepartmentId = parentDepartmentId;
    }

    public String getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getOpenDepartmentId() {
        return openDepartmentId;
    }

    public void setOpenDepartmentId(String openDepartmentId) {
        this.openDepartmentId = openDepartmentId;
    }

    public String getLeaderUserId() {
        return leaderUserId;
    }

    public void setLeaderUserId(String leaderUserId) {
        this.leaderUserId = leaderUserId;
    }

    public String getDepartmentOrder() {
        return departmentOrder;
    }

    public void setDepartmentOrder(String departmentOrder) {
        this.departmentOrder = departmentOrder;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLeaders() {
        return leaders;
    }

    public void setLeaders(String leaders) {
        this.leaders = leaders;
    }
}
