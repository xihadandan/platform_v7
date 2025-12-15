/*
 * @(#)2019年2月28日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.Separator;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2019年2月28日.1	zhulh		2019年2月28日		Create
 * </pre>
 * @date 2019年2月28日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewFlowConditionConfig extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 1362032754536065445L;

    private static final String TYPE_FORM_FIELD = "1";

    private static final String TYPE_GROUP = "3";

    private static final String TYPE_LOGIC = "4";

    private static final String TYPE_EXPRESSION = "5";

    // 条件类型
    private String type;
    // 连接符
    private String connector;
    // 左括号
    private String leftBracket;
    // 字段
    private String fieldName;
    // 操作符
    private String operator;
    // 值
    private String value;
    // 右括号
    private String rightBracket;
    // 群组类型
    private String groupType;
    // 群组值
    private String groupValue;
    // 表达式
    private String expression;

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type 要设置的type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the connector
     */
    public String getConnector() {
        return connector;
    }

    /**
     * @param connector 要设置的connector
     */
    public void setConnector(String connector) {
        this.connector = connector;
    }

    /**
     * @return the leftBracket
     */
    public String getLeftBracket() {
        return leftBracket;
    }

    /**
     * @param leftBracket 要设置的leftBracket
     */
    public void setLeftBracket(String leftBracket) {
        this.leftBracket = leftBracket;
    }

    /**
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * @param fieldName 要设置的fieldName
     */
    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    /**
     * @return the operator
     */
    public String getOperator() {
        return operator;
    }

    /**
     * @param operator 要设置的operator
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @param value 要设置的value
     */
    public void setValue(String value) {
        this.value = value;
    }

    /**
     * @return the rightBracket
     */
    public String getRightBracket() {
        return rightBracket;
    }

    /**
     * @param rightBracket 要设置的rightBracket
     */
    public void setRightBracket(String rightBracket) {
        this.rightBracket = rightBracket;
    }

    /**
     * @return the groupType
     */
    public String getGroupType() {
        return groupType;
    }

    /**
     * @param groupType 要设置的groupType
     */
    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    /**
     * @return the groupValue
     */
    public String getGroupValue() {
        return groupValue;
    }

    /**
     * @param groupValue 要设置的groupValue
     */
    public void setGroupValue(String groupValue) {
        this.groupValue = groupValue;
    }

    /**
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * @param expression 要设置的expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * @return
     */
    public String getCondition() {
        StringBuilder sb = new StringBuilder();
        sb.append(Separator.SPACE.getValue());
        sb.append(connector);
        sb.append(Separator.SPACE.getValue());
        switch (type) {
            case TYPE_FORM_FIELD:
                sb.append(leftBracket);
                sb.append("${dyform." + fieldName + "}");
                sb.append(Separator.SPACE.getValue());
                sb.append(operator);
                sb.append(Separator.SPACE.getValue());
                sb.append(value);
                sb.append(rightBracket);
                break;
            case TYPE_GROUP:
                sb.append(Separator.SPACE.getValue());
                sb.append(leftBracket);
                sb.append("'" + groupType + "'");
                sb.append(" NewFlowMemberOf ");
                sb.append("'" + groupValue + "'");
                sb.append(rightBracket);
                sb.append(Separator.SPACE.getValue());
                break;
            case TYPE_EXPRESSION:
                sb.append(leftBracket);
                sb.append(expression);
                sb.append(rightBracket);
                break;
            case TYPE_LOGIC:
                break;
            default:
                break;
        }
        return sb.toString();
    }

}
