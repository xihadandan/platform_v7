/*
 * @(#)2018年9月14日 V1.0
 *
 * Copyright 2018 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.data.enums;

/**
 * Description: 如何描述该类
 *
 * @author linxr
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2018年9月14日.1	linxr		2018年9月14日		Create
 * </pre>
 * @date 2018年9月14日
 */
public enum EnumFormFilterCondition {
    DEPARTMENT("D", "部门"), CURRENTUSER("C", "当前用户"), ORG("O", "组织单位");
    private String value;
    private String remark;

    private EnumFormFilterCondition(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public String getValue() {
        return value;
    }

    public String getRemark() {
        return remark;
    }
}
