/*
 * @(#)2015-9-29 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.support.validation;

import com.wellsoft.pt.dyform.facade.dto.DyFormData;

import java.util.List;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-9-29.1	zhulh		2015-9-29		Create
 * </pre>
 * @date 2015-9-29
 */
public class DyformDataValidationResult implements ValidationResult {

    private final DyFormData dyFormData;

    private final List<DyformFieldError> errors;

    /**
     * @param dyFormData
     * @param errors
     */
    public DyformDataValidationResult(DyFormData dyFormData, List<DyformFieldError> errors) {
        super();
        this.dyFormData = dyFormData;
        this.errors = errors;
    }

    /**
     * @return the dyFormData
     */
    public DyFormData getDyFormData() {
        return dyFormData;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.support.validation.ValidationResult#hasErrors()
     */
    @Override
    public boolean hasErrors() {
        return errors != null && !errors.isEmpty();
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.dyform.support.validation.ValidationResult#getErrors()
     */
    @Override
    public List<DyformFieldError> getErrors() {
        return errors;
    }

}
