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
public class OrgElementAttr implements Serializable {
    @FieldType(desc = "属性名称")
    private String name;
    @FieldType(desc = "属性编码")
    private String code;
    @FieldType(desc = "属性类型")
    private String attrType;
    @FieldType(desc = "属性值")
    private String attrValue;

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

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getAttrValue() {
        return attrValue;
    }

    public void setAttrValue(String attrValue) {
        this.attrValue = attrValue;
    }
}
