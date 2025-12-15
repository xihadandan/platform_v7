/*
 * @(#)2019年8月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.validator;

import java.io.Serializable;
import java.util.Map;

public class ValidatorParams implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private Validator validator;
    private Map<String, Object> params;

    /**
     * 如何描述该构造方法
     */
    public ValidatorParams() {
        super();
    }

    /**
     * 如何描述该构造方法
     *
     * @param validator
     */
    public ValidatorParams(Validator validator) {
        super();
        this.validator = validator;
    }

    /**
     * @return the validator
     */
    public Validator getValidator() {
        return validator;
    }

    /**
     * @param validator 要设置的validator
     */
    public void setValidator(Validator validator) {
        this.validator = validator;
    }

    /**
     * @return the params
     */
    public Map<String, Object> getParams() {
        return params;
    }

    /**
     * @param params 要设置的params
     */
    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

}