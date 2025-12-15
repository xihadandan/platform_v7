package com.wellsoft.pt.ei.dto.org;

import com.wellsoft.pt.ei.annotate.FieldType;

import java.io.Serializable;

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
public class OrgTypeData implements Serializable {
    @FieldType(desc = "主键uuid")
    private String uuid;
    @FieldType(desc = "组织类型名称")
    private String name;
    @FieldType(desc = "组织类型Id")
    private String id;
    @FieldType(desc = "编码")
    private String code;
    @FieldType(desc = "备注")
    private String remark;
    @FieldType(desc = "是否禁用", dictValue = "1：禁用；0：正常")
    private Integer isForbidden;
//    private String isForbiddenVal;

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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Integer getIsForbidden() {
        return isForbidden;
    }

    public void setIsForbidden(Integer isForbidden) {
        this.isForbidden = isForbidden;
    }

    /*public String getIsForbiddenVal() {
        return isForbiddenVal;
    }

    public void setIsForbiddenVal(String isForbiddenVal) {
        this.isForbiddenVal = isForbiddenVal;
    }*/
}
