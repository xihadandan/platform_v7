/*
 * @(#)2019年8月17日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.validator;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhongzh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年8月17日.1	zhongzh		2019年8月17日		Create
 * </pre>
 * @date 2019年8月17日
 */
public class ValidatorResult implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private boolean isValid;
    private String validMeg;

    /**
     * 如何描述该构造方法
     *
     * @param isValid
     */
    public ValidatorResult(boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * 如何描述该构造方法
     *
     * @param isValid
     * @param validMeg
     */
    public ValidatorResult(boolean isValid, String validMeg) {
        this.isValid = isValid;
        this.validMeg = validMeg;
    }

    /**
     * @return the isValid
     */
    public boolean isValid() {
        return isValid;
    }

    /**
     * @param isValid 要设置的isValid
     */
    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    /**
     * @return the validMeg
     */
    public String getValidMeg() {
        return validMeg;
    }

    /**
     * @param validMeg 要设置的validMeg
     */
    public void setValidMeg(String validMeg) {
        this.validMeg = validMeg;
    }

}
