package com.wellsoft.pt.dyform.implement.data.enums;

/**
 * Description: 数据验证异常代码
 *
 * @author Administrator
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2014-7-17.1	Administrator		2014-7-17		Create
 * </pre>
 * @date 2014-7-17
 */
public enum EnumValidateCode {
    NULL(""), //
    EMPTY(""), //
    //	INVALID_FORMAT_DATE("日期格式错误"), //
    //	INVALID_FORMAT_LONG("日期格式错误"), //
    //	INVALID_FORMAT_FLOAT("日期格式错误"), //
    //	INVALID_FORMAT_INT("日期格式错误"), //
    //	INVALID_FORMAT_FILE("日期格式错误"), //
    //	INVALID_FORMAT_VALUEMAP("JSON值格式错误"), //对于真实值显示值
    FAIL("验证失败"), SUCESS("验证通过");

    private String remark;

    private EnumValidateCode(String remark) {
        this.remark = remark;
    }

    public String getRemark() {
        return remark;
    }

}
