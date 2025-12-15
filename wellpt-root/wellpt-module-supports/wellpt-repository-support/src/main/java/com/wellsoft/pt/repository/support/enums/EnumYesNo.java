package com.wellsoft.pt.repository.support.enums;

/**
 * Description: 是否
 *
 * @author hunt
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-1-5.1	hunt		2014-4-18		Create
 * </pre>
 * @date 2014-4-18
 */
public enum EnumYesNo {
    YES("YES", "是"), NO("NO", "否");
    private String value = "";
    private String remark;

    private EnumYesNo(String value, String remark) {
        this.value = value;
        this.remark = remark;
    }

    public String getValue() {
        return value;
    }

    public String getRemark() {
        return remark;
    }


    public EnumYesNo value2EnumObj(String value) {
        EnumYesNo enumObj = null;
        for (EnumYesNo yesno : EnumYesNo.values()) {
            if (yesno.getValue().equals(value)) {
                enumObj = yesno;
            }
        }

        return enumObj;
    }


}
