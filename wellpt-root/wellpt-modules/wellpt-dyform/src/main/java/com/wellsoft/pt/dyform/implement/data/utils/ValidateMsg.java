package com.wellsoft.pt.dyform.implement.data.utils;

import com.wellsoft.pt.dyform.implement.data.enums.EnumValidateCode;
import com.wellsoft.pt.dyform.implement.definition.validator.ValidateData;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Description: 验证信息
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月19日.1	zhongzh		2019年8月19日		Create
 * </pre>
 * @date 2019年8月19日
 */
public class ValidateMsg implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private EnumValidateCode code = EnumValidateCode.SUCESS;
    private String msg;

    private List<ValidateData> errors = new ArrayList<ValidateData>();

    /**
     * @return the code
     */
    public EnumValidateCode getCode() {
        return code;
    }

    /**
     * @param code 要设置的code
     */
    public void setCode(EnumValidateCode code) {
        this.code = code;
    }

    /**
     * @return the msg
     */
    public String getMsg() {
        return msg;
    }

    /**
     * @param msg 要设置的msg
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * @return the errors
     */
    public List<ValidateData> getErrors() {
        return errors;
    }

    /**
     * @param errors 要设置的errors
     */
    public void setErrors(List<ValidateData> errors) {
        this.errors = errors;
    }

}
