/*
 * @(#)2015-9-28 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.support.validation.validator;

import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import com.wellsoft.pt.dyform.support.validation.Unique;
import com.wellsoft.pt.dyform.support.validation.ValidationRuleModel;
import com.wellsoft.pt.dyform.support.validation.ValidationRuleModelContextHolder;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-9-28.1	zhulh		2015-9-28		Create
 * </pre>
 * @date 2015-9-28
 */
public class UniqueValidator implements ConstraintValidator<Unique, Object> {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * (non-Javadoc)
     *
     * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
     */
    @Override
    public void initialize(Unique uniqueAnnotation) {
    }

    /**
     * (non-Javadoc)
     *
     * @see javax.validation.ConstraintValidator#isValid(java.lang.Object, javax.validation.ConstraintValidatorContext)
     */
    @Override
    public boolean isValid(Object value, ConstraintValidatorContext constraintValidatorContext) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return true;
        }

        ValidationRuleModel validationRuleModel = ValidationRuleModelContextHolder.getValidationRuleModel();
        String tableName = validationRuleModel.getTableName();

        String checkType = tableName;
        String uuid = validationRuleModel.getDataUuid();
        String fieldName = validationRuleModel.getFieldName();
        String fieldValue = value.toString();

        DyFormFacade dyFormApiFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
        boolean isExist = false;
        if (StringUtils.isBlank(uuid)) {
            try {
                isExist = dyFormApiFacade.queryFormDataExists(checkType, fieldName, fieldValue);
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        } else {
            try {
                isExist = dyFormApiFacade.queryFormDataExists(checkType, fieldName, fieldValue, uuid);
            } catch (Exception e) {
                logger.error(ExceptionUtils.getStackTrace(e));
            }
        }
        return !isExist;
    }

}
