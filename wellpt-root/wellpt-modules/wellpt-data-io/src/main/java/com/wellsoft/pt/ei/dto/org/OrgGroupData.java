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
public class OrgGroupData implements Serializable {
    @FieldType(desc = "主键uuid")
    private String uuid;
    @FieldType(desc = "群组名称", required = true)
    private String name;
    @FieldType(desc = "编码", required = true)
    private String code;
    @FieldType(desc = "群组Id")
    private String id;
    @FieldType(desc = "群组成员Id路径", required = true)
    private String memberIdPaths;
    @FieldType(desc = "群组成员名称", required = true)
    private String memberNames;
    @FieldType(desc = "备注")
    private String remark;
    @FieldType(desc = "群组类型", type = ExportFieldTypeEnum.INTEGER, dictValue = "1：个人群组，0：公共群组", required = true)
    private Integer type;
    //    private String typeVal;
    @FieldType(desc = "群组成员信息", required = true)
    private List<OrgGroupMember> orgGroupMembers;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMemberIdPaths() {
        return memberIdPaths;
    }

    public void setMemberIdPaths(String memberIdPaths) {
        this.memberIdPaths = memberIdPaths;
    }

    public String getMemberNames() {
        return memberNames;
    }

    public void setMemberNames(String memberNames) {
        this.memberNames = memberNames;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    /*public String getTypeVal() {
        return typeVal;
    }

    public void setTypeVal(String typeVal) {
        this.typeVal = typeVal;
    }*/

    public List<OrgGroupMember> getOrgGroupMembers() {
        return orgGroupMembers;
    }

    public void setOrgGroupMembers(List<OrgGroupMember> orgGroupMembers) {
        this.orgGroupMembers = orgGroupMembers;
    }
}
