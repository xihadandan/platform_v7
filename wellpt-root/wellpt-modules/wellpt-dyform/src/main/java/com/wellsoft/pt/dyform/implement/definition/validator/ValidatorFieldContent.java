/*
 * @(#)2019年8月19日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.implement.definition.validator;

import com.wellsoft.pt.dyform.implement.definition.dto.FieldDefinition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ValidatorFieldContent implements Serializable {
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1L;
    private FieldDefinition fieldDefinition;
    private List<ValidatorParams> validators = new ArrayList<ValidatorParams>();

    /**
     * @return the fieldDefinition
     */
    public FieldDefinition getFieldDefinition() {
        return fieldDefinition;
    }

    /**
     * @param fieldDefinition 要设置的fieldDefinition
     */
    public void setFieldDefinition(FieldDefinition fieldDefinition) {
        this.fieldDefinition = fieldDefinition;
    }

    /**
     * @return the validators
     */
    public List<ValidatorParams> getValidators() {
        return validators;
    }

    /**
     * @param validators 要设置的validators
     */
    public void setValidators(List<ValidatorParams> validators) {
        this.validators = validators;
    }

}