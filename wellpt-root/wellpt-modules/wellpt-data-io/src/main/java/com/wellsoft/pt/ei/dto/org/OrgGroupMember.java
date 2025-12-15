package com.wellsoft.pt.ei.dto.org;

import com.wellsoft.pt.ei.annotate.FieldType;

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
public class OrgGroupMember {
    @FieldType(desc = "成员对象Id")
    private String memberObjId;
    @FieldType(desc = "成员对象名称")
    private String memberObjName;
    @FieldType(desc = "成员对象类型", dictValue = "U：用户；J：职位；O：组织节点；D：部门；B：业务单位")
    private String memberObjType;
    //    private String memberObjTypeVal;
    @FieldType(desc = "成员对象uuid")
    private String memberObjUuid;

    public String getMemberObjId() {
        return memberObjId;
    }

    public void setMemberObjId(String memberObjId) {
        this.memberObjId = memberObjId;
    }

    public String getMemberObjName() {
        return memberObjName;
    }

    public void setMemberObjName(String memberObjName) {
        this.memberObjName = memberObjName;
    }

    public String getMemberObjType() {
        return memberObjType;
    }

    public void setMemberObjType(String memberObjType) {
        this.memberObjType = memberObjType;
    }

    public String getMemberObjUuid() {
        return memberObjUuid;
    }

    public void setMemberObjUuid(String memberObjUuid) {
        this.memberObjUuid = memberObjUuid;
    }

    /*public String getMemberObjTypeVal() {
        return memberObjTypeVal;
    }

    public void setMemberObjTypeVal(String memberObjTypeVal) {
        this.memberObjTypeVal = memberObjTypeVal;
    }*/
}
