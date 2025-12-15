package com.wellsoft.pt.ei.dto.org;

import com.wellsoft.pt.ei.annotate.FieldType;
import com.wellsoft.pt.ei.constants.ExportFieldTypeEnum;

import java.io.Serializable;
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
public class OrgVersionConf implements Serializable {
    @FieldType(desc = "配置信息类型", dictValue = "O：组织节点；B：业务单位；D：部门；J：职位；V：引用系统单位；", required = true)
    private String type;
    @FieldType(desc = "组织元素uuid")
    private String eleUuid;
    @FieldType(desc = "名称", required = true)
    private String name;
    @FieldType(desc = "简称")
    private String shortName;
    @FieldType(desc = "组织元素id")
    private String eleId;
    @FieldType(desc = "组织元素id路径")
    private String eleIdPath;
    @FieldType(desc = "编码", required = true)
    private String code;
    @FieldType(desc = "父级id路径")
    private String parentEleIdPath;
    @FieldType(desc = "父级名称路径", required = true)
    private String parentEleNamePath;
    @FieldType(desc = "备注")
    private String remark;
    @FieldType(desc = "sap编码")
    private String sapCode;
    @FieldType(desc = "负责人id路径")
    private String bossIdPaths;
    @FieldType(desc = "负责人名称路径")
    private String bossNames;
    @FieldType(desc = "管理员id路径")
    private String managerIdPaths;
    @FieldType(desc = "管理员名称路径")
    private String managerNames;
    @FieldType(desc = "分管领导Id路径")
    private String branchLeaderIdPaths;
    @FieldType(desc = "分管领导名称")
    private String branchLeaderNames;
    @FieldType(desc = "归属职务")
    private String dutyId;
    @FieldType(desc = "配置", dictValue = "0：固定当前版本；1：自动更新到当前版本", type = ExportFieldTypeEnum.INTEGER)
    private Integer autoUpgrade;
    @FieldType(desc = "自定义属性")
    private List<OrgElementAttr> orgElementAttrs;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getEleUuid() {
        return eleUuid;
    }

    public void setEleUuid(String eleUuid) {
        this.eleUuid = eleUuid;
    }

    public String getEleId() {
        return eleId;
    }

    public void setEleId(String eleId) {
        this.eleId = eleId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getEleIdPath() {
        return eleIdPath;
    }

    public void setEleIdPath(String eleIdPath) {
        this.eleIdPath = eleIdPath;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getParentEleIdPath() {
        return parentEleIdPath;
    }

    public void setParentEleIdPath(String parentEleIdPath) {
        this.parentEleIdPath = parentEleIdPath;
    }

    public String getParentEleNamePath() {
        return parentEleNamePath;
    }

    public void setParentEleNamePath(String parentEleNamePath) {
        this.parentEleNamePath = parentEleNamePath;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getSapCode() {
        return sapCode;
    }

    public void setSapCode(String sapCode) {
        this.sapCode = sapCode;
    }

    public String getBossIdPaths() {
        return bossIdPaths;
    }

    public void setBossIdPaths(String bossIdPaths) {
        this.bossIdPaths = bossIdPaths;
    }

    public String getBossNames() {
        return bossNames;
    }

    public void setBossNames(String bossNames) {
        this.bossNames = bossNames;
    }

    public String getManagerIdPaths() {
        return managerIdPaths;
    }

    public void setManagerIdPaths(String managerIdPaths) {
        this.managerIdPaths = managerIdPaths;
    }

    public String getManagerNames() {
        return managerNames;
    }

    public void setManagerNames(String managerNames) {
        this.managerNames = managerNames;
    }

    public String getBranchLeaderIdPaths() {
        return branchLeaderIdPaths;
    }

    public void setBranchLeaderIdPaths(String branchLeaderIdPaths) {
        this.branchLeaderIdPaths = branchLeaderIdPaths;
    }

    public String getBranchLeaderNames() {
        return branchLeaderNames;
    }

    public void setBranchLeaderNames(String branchLeaderNames) {
        this.branchLeaderNames = branchLeaderNames;
    }

    public String getDutyId() {
        return dutyId;
    }

    public void setDutyId(String dutyId) {
        this.dutyId = dutyId;
    }

    public Integer getAutoUpgrade() {
        return autoUpgrade;
    }

    public void setAutoUpgrade(Integer autoUpgrade) {
        this.autoUpgrade = autoUpgrade;
    }

    public List<OrgElementAttr> getOrgElementAttrs() {
        return orgElementAttrs;
    }

    public void setOrgElementAttrs(List<OrgElementAttr> orgElementAttrs) {
        this.orgElementAttrs = orgElementAttrs;
    }
}
