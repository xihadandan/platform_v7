/*
 * @(#)2015-9-28 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dyform.support.validation;

import com.wellsoft.context.util.json.JsonUtils;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import org.apache.commons.lang.StringUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

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
public class DyformDataValidationUtils {

    private static ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    private static Validator validator = factory.getValidator();

    public static ValidationResult validate(DyFormData dyFormData) {
        List<DyformFieldError> errors = new ArrayList<DyformFieldError>();
        // 验证主表
        validateDyformData(errors, dyFormData, false);

        // 从表验证
        String formUuid = dyFormData.getFormUuid();
        Map<String, List<Map<String, Object>>> formDatas = dyFormData.getFormDatas();
        for (String key : formDatas.keySet()) {
            if (key.equals(formUuid)) {
                continue;
            }
            for (Map<String, Object> map : formDatas.get(key)) {
                String subDataUuid = (String) map.get("uuid");
                DyFormData subDyFormData = dyFormData.getDyFormData(key, subDataUuid);
                validateDyformData(errors, subDyFormData, true);
            }
        }

        // 验证结果
        ValidationResult validationResult = new DyformDataValidationResult(dyFormData, errors);
        return validationResult;
    }

    /**
     * 如何描述该方法
     *
     * @param dyFormData
     * @param errors
     * @return
     */
    private static void validateDyformData(List<DyformFieldError> errors, DyFormData dyFormData, boolean isSubformField) {
        String formDefinition = dyFormData.getFormDefinition();
        FormDefinitionRuleModel formDefinitionRuleModel = JsonUtils.json2Object(formDefinition,
                FormDefinitionRuleModel.class);
        ValidationRuleModel validationRuleModel = new ValidationRuleModel();
        validationRuleModel.setFormId(formDefinitionRuleModel.getFormId());
        validationRuleModel.setDataUuid(dyFormData.getDataUuid());
        validationRuleModel.setTableName(formDefinitionRuleModel.getTableName());
        // 主表验证
        Map<String, FieldDefinitionRuleModel> fields = formDefinitionRuleModel.getFields();
        for (String key : fields.keySet()) {
            FieldDefinitionRuleModel fdrm = fields.get(key);
            String fieldName = fdrm.getName();
            List<Map<String, String>> rules = fdrm.getFieldCheckRules();
            Object validateValue = dyFormData.getFieldValue(fieldName);
            validationRuleModel.setFieldName(fieldName);
            // 长度验证
            String length = fdrm.getLength();
            if (StringUtils.isNumeric(length)) {
                validationRuleModel.setLength(Integer.valueOf(length));
                Map<String, String> lengthRule = new HashMap<String, String>();
                lengthRule.put("value", "100");
                rules.add(lengthRule);
            }
            for (Map<String, String> rule : rules) {
                String value = rule.get("value");
                if (!StringUtils.isNumeric(value)) {
                    continue;
                }
                String validateField = null;
                int valueInt = Integer.valueOf(value).intValue();
                switch (valueInt) {
                    case 100:
                        // 最大长度
                        validateField = ValidationRuleModel.MAX_LENGTH;
                        break;
                    case 1:
                        // 非空
                        validateField = ValidationRuleModel.NOT_BLANK;
                        break;
                    case 5:
                        // 唯一
                        validateField = ValidationRuleModel.UNIQUE;
                        break;
                    case 10:
                        // 普通
                        break;
                    case 11:
                        // URL
                        validateField = ValidationRuleModel.URL;
                        break;
                    case 12:
                        // EMAIL
                        validateField = ValidationRuleModel.EMAIL;
                        break;
                    case 13:
                        // 身份证
                        validateField = ValidationRuleModel.ID_CARD_NUMBER;
                        break;
                    case 14:
                        // 电话
                        validateField = ValidationRuleModel.TELEPHONE;
                        break;
                    case 15:
                        // 手机
                        validateField = ValidationRuleModel.MOBILE_PHONE;
                        break;
                    case 17:
                        // 邮编
                        validateField = ValidationRuleModel.ZIP_CODE;
                        break;
                    default:
                        break;
                }

                if (StringUtils.isBlank(validateField)) {
                    continue;
                }

                // 验证处理
                ValidationRuleModelContextHolder.setValidationRuleModel(validationRuleModel);
                Set<ConstraintViolation<ValidationRuleModel>> modelConstraintViolations = validator.validateValue(
                        ValidationRuleModel.class, validateField, validateValue);
                for (ConstraintViolation<ValidationRuleModel> constraintViolation : modelConstraintViolations) {
                    errors.add(convertToFieldError(constraintViolation, validationRuleModel, validateField,
                            isSubformField));
                }
                ValidationRuleModelContextHolder.clear();
            }
        }
    }

    /**
     * 如何描述该方法
     *
     * @param constraintViolation
     * @return
     */
    private static DyformFieldError convertToFieldError(ConstraintViolation<ValidationRuleModel> constraintViolation,
                                                        ValidationRuleModel validationRuleModel, String rule, boolean isSubformField) {
        String fieldName = validationRuleModel.getFieldName();
        Object rejectedValue = constraintViolation.getInvalidValue();
        String checkedRule = rule;
        String message = constraintViolation.getMessage();
        String formId = validationRuleModel.getFormId();
        String dataUuid = validationRuleModel.getDataUuid();
        DyformFieldError fieldError = new DyformFieldError(fieldName, rejectedValue, checkedRule, message, formId,
                dataUuid, isSubformField);
        return fieldError;
    }

}
