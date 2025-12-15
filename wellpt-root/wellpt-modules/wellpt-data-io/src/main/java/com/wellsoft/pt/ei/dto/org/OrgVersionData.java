package com.wellsoft.pt.ei.dto.org;

import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author liuyz
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2021/9/28.1	liuyz		2021/9/28		Create
 * </pre>
 * @date 2021/9/28
 */
public class OrgVersionData implements Serializable {
    @FieldType(desc = "主键uuid")
    private String uuid;
    @FieldType(desc = "名称")
    private String name;
    @FieldType(desc = "版本Id")
    private String id;
    @FieldType(desc = "职能类型")
    private String functionType;
    @FieldType(desc = "职能类型对应的名称")
    private String functionTypeName;
    @FieldType(desc = "备注")
    private String remark;
    @FieldType(desc = "归属系统单位名称")
    private String initSystemUnitName;
    @FieldType(desc = "归属系统单位Id")
    private String systemUnitId;
    @FieldType(desc = "组织版本")
    private String version;
    @FieldType(desc = "来源版本uuid")
    private String sourceVersionUuid;
    @FieldType(desc = "是否默认", dictValue = "1：是；0：否", type = ExportFieldTypeEnum.BOOLEAN)
    private boolean isDefault;
    @FieldType(desc = "创建时间", type = ExportFieldTypeEnum.DATE)
    private Date createTime;
    @FieldType(desc = "状态", dictValue = "0：不启用；1：启用", type = ExportFieldTypeEnum.INTEGER)
    private Integer status;
    @FieldType(desc = "组织版本配置信息-组织节点", isGroup = true)
    private List<OrgVersionConf> orgNodes;
    @FieldType(desc = "组织版本配置信息-业务单位", isGroup = true)
    private List<OrgVersionConf> businessUnits;
    @FieldType(desc = "组织版本配置信息-部门", isGroup = true)
    private List<OrgVersionConf> departments;
    @FieldType(desc = "组织版本配置信息-职位", isGroup = true)
    private List<OrgVersionConf> jobs;
    @FieldType(desc = "组织版本配置信息-引用系统单位", isGroup = true)
    private List<OrgVersionConf> orgVersions;

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFunctionType() {
        return functionType;
    }

    public void setFunctionType(String functionType) {
        this.functionType = functionType;
    }

    public String getFunctionTypeName() {
        return functionTypeName;
    }

    public void setFunctionTypeName(String functionTypeName) {
        this.functionTypeName = functionTypeName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getInitSystemUnitName() {
        return initSystemUnitName;
    }

    public void setInitSystemUnitName(String initSystemUnitName) {
        this.initSystemUnitName = initSystemUnitName;
    }

    public String getSystemUnitId() {
        return systemUnitId;
    }

    public void setSystemUnitId(String systemUnitId) {
        this.systemUnitId = systemUnitId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getSourceVersionUuid() {
        return sourceVersionUuid;
    }

    public void setSourceVersionUuid(String sourceVersionUuid) {
        this.sourceVersionUuid = sourceVersionUuid;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<OrgVersionConf> getOrgNodes() {
        return orgNodes;
    }

    public void setOrgNodes(List<OrgVersionConf> orgNodes) {
        this.orgNodes = orgNodes;
    }

    public List<OrgVersionConf> getBusinessUnits() {
        return businessUnits;
    }

    public void setBusinessUnits(List<OrgVersionConf> businessUnits) {
        this.businessUnits = businessUnits;
    }

    public List<OrgVersionConf> getDepartments() {
        return departments;
    }

    public void setDepartments(List<OrgVersionConf> departments) {
        this.departments = departments;
    }

    public List<OrgVersionConf> getJobs() {
        return jobs;
    }

    public void setJobs(List<OrgVersionConf> jobs) {
        this.jobs = jobs;
    }

    public List<OrgVersionConf> getOrgVersions() {
        return orgVersions;
    }

    public void setOrgVersions(List<OrgVersionConf> orgVersions) {
        this.orgVersions = orgVersions;
    }
}
