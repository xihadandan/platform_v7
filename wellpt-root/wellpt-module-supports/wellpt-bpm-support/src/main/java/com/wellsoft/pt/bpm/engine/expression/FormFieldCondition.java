/*
 * @(#)2013-4-26 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.expression;

import com.google.common.collect.Maps;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.context.util.ApplicationContextHolder;
import com.wellsoft.pt.bpm.engine.core.Token;
import com.wellsoft.pt.bpm.engine.node.Node;
import com.wellsoft.pt.bpm.engine.support.TaskData;
import com.wellsoft.pt.dyform.facade.dto.DyFormData;
import com.wellsoft.pt.dyform.facade.service.DyFormFacade;
import ognl.Ognl;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;

import java.util.List;
import java.util.Map;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-4-26.1	zhulh		2013-4-26		Create
 * </pre>
 * @date 2013-4-26
 */
public class FormFieldCondition extends AbstractCondition {

    private String prefix = "";

    private String suffix = "";

    /**
     * @param expression
     */
    public FormFieldCondition(String expression) {
        String exp = StringUtils.trim(expression);
        if (exp.startsWith("& (") || exp.startsWith("| (")) {
            prefix = exp.substring(0, 3);
            exp = exp.substring(3);
        } else if (exp.startsWith("& ") || exp.startsWith("| ")) {
            prefix = exp.substring(0, 2);
            exp = exp.substring(2);
        } else if (exp.startsWith("&") || exp.startsWith("|")) {
            prefix = exp.substring(0, 1);
            exp = exp.substring(1);
        }

        if (exp.startsWith("(") && !exp.endsWith(")")) {
            prefix += exp.substring(0, 1);
            exp = exp.substring(1);
        }

        if (!exp.startsWith("(") && exp.endsWith(")")) {
            suffix += exp.substring(exp.length() - 1, exp.length());
            exp = exp.substring(0, exp.length() - 1);
        }

        if (exp.startsWith("(") && exp.endsWith(")")) {
            prefix += "(";
            suffix += ")";
            exp = exp.substring(1, exp.length() - 1);
        }

        setExpression(exp);
    }

    /**
     * @param expression
     * @return
     */
    private static String getFormKey(String expression) {
        if (expression.lastIndexOf(GT) != -1) {
            return StringUtils.trim(expression.substring(0, expression.lastIndexOf(GT) - 1));
        }
        if (expression.lastIndexOf(GEQ) != -1) {
            return StringUtils.trim(expression.substring(0, expression.lastIndexOf(GEQ) - 1));
        }
        if (expression.lastIndexOf(LT) != -1) {
            return StringUtils.trim(expression.substring(0, expression.lastIndexOf(LT) - 1));
        }
        if (expression.lastIndexOf(LEQ) != -1) {
            return StringUtils.trim(expression.substring(0, expression.lastIndexOf(LEQ) - 1));
        }
        if (expression.lastIndexOf(EQ) != -1) {
            return StringUtils.trim(expression.substring(0, expression.lastIndexOf(EQ) - 1));
        }
        if (expression.lastIndexOf(NEQ) != -1) {
            return StringUtils.trim(expression.substring(0, expression.lastIndexOf(NEQ) - 1));
        }
        if (expression.lastIndexOf(LIKE) != -1) {
            return StringUtils.trim(expression.substring(0, expression.lastIndexOf(LIKE) - 1));
        }
        if (expression.lastIndexOf(NOLIKE) != -1) {
            return StringUtils.trim(expression.substring(0, expression.lastIndexOf(NOLIKE) - 1));
        }
        return expression;
    }

    /**
     * @param regex
     * @param expression
     * @param beginIndex
     * @return
     */
    private static String getFormValue(String regex, String expression, int beginIndex, Map<String, Object> root) {
        String formValue = StringUtils.trim(expression.substring(expression.lastIndexOf(regex) + regex.length(),
                expression.length()));
        // 字段值标识常量、表单字段的处理
        if (StringUtils.endsWith(formValue, ":1") || StringUtils.endsWith(formValue, ":2")) {
            String formValueType = StringUtils.substring(formValue, formValue.length() - 1);
            // 常量值或表单字段值
            formValue = StringUtils.substring(formValue, 0, formValue.length() - 2);
            // 表单字段值
            if (StringUtils.equals(formValueType, VALUE_TYPE_FORM_FIELD)) {
                formValue = ObjectUtils.toString(root.get(formValue));
            }
        }
        if (StringUtils.startsWith(formValue, "'") && StringUtils.endsWith(formValue, "'")) {
            formValue = StringUtils.substring(formValue, 1, formValue.length() - 1);
        }
        if (StringUtils.startsWith(formValue, "\"") && StringUtils.endsWith(formValue, "\"")) {
            formValue = StringUtils.substring(formValue, 1, formValue.length() - 1);
        }
        return formValue;
    }

    /**
     * (non-Javadoc)
     *
     * @see com.wellsoft.pt.bpm.engine.expression.Condition#evaluate(com.wellsoft.pt.bpm.engine.core.Token)
     */
    @Override
    public String evaluate(Token token, Node to) {
        TaskData taskData = token.getTaskData();
        DyFormData dyFormData = taskData.getDyFormData(taskData.getDataUuid());
        String expression = StringUtils.trim(this.getExpression());
        String fieldName = getFormKey(expression);
        if (isSubformField(fieldName)) {
            String[] subformFieldInfos = StringUtils.split(fieldName, ":");
            String subformUuid = subformFieldInfos[0];
            String subformFieldName = subformFieldInfos[1];
            expression = StringUtils.replace(expression, subformUuid + ":", StringUtils.EMPTY);
            DyFormFacade dyFormFacade = ApplicationContextHolder.getBean(DyFormFacade.class);
            List<Map<String, Object>> subformDatas = dyFormData.getFormDatasById(dyFormFacade
                    .getFormIdByFormUuid(subformUuid));
            // 从表数据比较，一个成立就返回true
            if (CollectionUtils.isNotEmpty(subformDatas)) {
                for (Map<String, Object> subformData : subformDatas) {
                    if (evaluateFormField(expression, subformData, subformFieldName)) {
                        return prefix + true + suffix;
                    }
                }
            }
        } else {
            Map<String, Object> mainFormData = dyFormData.getFormDataOfMainform();
            if (mainFormData == null) {
                mainFormData = Maps.newHashMap();
            }
            return prefix + evaluateFormField(expression, mainFormData, fieldName) + suffix;
        }
        return prefix + false + suffix;
    }

    /**
     * 字段值比较
     *
     * @return
     */
    private boolean evaluateFormField(String expression, Map<String, Object> root, String fieldName) {
        boolean evaluateResult = false;
        try {
            if (expression.indexOf(NOLIKE) != -1) {
                String key = fieldName;// getFormKey(NOLIKE, expression, 1);
                String value = getFormValue(NOLIKE, expression, 7, root);
                if (StringUtils.isNotBlank(key)) {
                    Object formValue = root.get(key);
                    if (formValue != null && formValue.toString().indexOf(value) != -1) {
                        evaluateResult = false;
                    } else {
                        evaluateResult = true;
                    }
                } else {
                    evaluateResult = false;
                }
            } else if (expression.indexOf(LIKE) != -1) {
                String key = fieldName;// getFormKey(LIKE, expression, 1);
                String value = getFormValue(LIKE, expression, 5, root);
                if (StringUtils.isNotBlank(key)) {
                    Object formValue = root.get(key);
                    if (formValue != null && formValue.toString().indexOf(value) != -1) {
                        evaluateResult = true;
                    } else {
                        evaluateResult = false;
                    }
                } else {
                    evaluateResult = false;
                }
            } else {
                String newExpression = updateFormValueIfRequired(expression, root);
                Object result = Ognl.getValue(newExpression, root);
                evaluateResult = (result instanceof Boolean) ? (Boolean) result : Integer.valueOf(1).equals(result);
            }
        } catch (Exception e) {
            logger.error(ExceptionUtils.getStackTrace(e));
            evaluateResult = false;
        }
        return evaluateResult;
    }

    /**
     * @param fieldName
     * @return
     */
    private boolean isSubformField(String fieldName) {
        return StringUtils.contains(fieldName, ":");
    }

    /**
     * @param expression
     * @param root
     * @return
     */
    private String updateFormValueIfRequired(String expression, Map<String, Object> root) {
        if (StringUtils.endsWith(expression, ":1") || StringUtils.endsWith(expression, ":2")) {
            String[] parts = StringUtils.split(expression, Separator.SPACE.getValue());
            int formValueIndex = parts.length - 1;
            // 常量值或表单字段值
            String formValue = StringUtils.substring(parts[formValueIndex], 0, parts[formValueIndex].length() - 2);
            parts[parts.length - 1] = formValue;
            return StringUtils.join(parts, Separator.SPACE.getValue());
        }
        return expression;
    }

}
