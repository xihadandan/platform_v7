/*
 * @(#)2015-4-1 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.org.support;

import com.wellsoft.context.enums.Separator;
import org.apache.commons.lang.StringUtils;

import java.io.Serializable;

/**
 * Description: 表达式配置信息
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015-4-1.1	zhulh		2015-4-1		Create
 * </pre>
 * @date 2015-4-1
 */
public class DutyAgentConditionConfig implements Serializable {

    // (zrbmlx == '{"1":"内部"}')
    // | (zrbmlx == '{"2":"外部"}')
    public static final String CONTAINS = "contains";
    public static final String NOT_CONTAINS = "not contains";
    public static final String FORM_FIELD = "1";
    public static final String ORG_ID = "2";
    public static final String EXPRESSION = "3";
    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 8974033647940211864L;
    // 表达式类型(1表单域值 2人员归属 3表达式)
    private String type;
    // 连接符
    private String connector;
    // 左括号
    private String leftBracket;
    // 运算符
    private String operator;
    // 变量1类型
    private String variable1Type;
    // 变量1名称
    private String variable1Name;
    // 变量1
    private String variable1;
    // 变量2类型
    private String variable2Type;
    // 变量2名称
    private String variable2Name;
    // 变量2
    private String variable2;
    // 右括号
    private String rightBracket;
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
     * @return the variable1Type
     */
    public String getVariable1Type() {
        return variable1Type;
    }

    /**
     * @param variable1Type 要设置的variable1Type
     */
    public void setVariable1Type(String variable1Type) {
        this.variable1Type = variable1Type;
    }

    /**
     * @return the variable1Name
     */
    public String getVariable1Name() {
        return variable1Name;
    }

    /**
     * @param variable1Name 要设置的variable1Name
     */
    public void setVariable1Name(String variable1Name) {
        this.variable1Name = variable1Name;
    }

    /**
     * @return the variable1
     */
    public String getVariable1() {
        return variable1;
    }

    /**
     * @param variable1 要设置的variable1
     */
    public void setVariable1(String variable1) {
        this.variable1 = variable1;
    }

    /**
     * @return the variable2Type
     */
    public String getVariable2Type() {
        return variable2Type;
    }

    /**
     * @param variable2Type 要设置的variable2Type
     */
    public void setVariable2Type(String variable2Type) {
        this.variable2Type = variable2Type;
    }

    /**
     * @return the variable2Name
     */
    public String getVariable2Name() {
        return variable2Name;
    }

    /**
     * @param variable2Name 要设置的variable2Name
     */
    public void setVariable2Name(String variable2Name) {
        this.variable2Name = variable2Name;
    }

    /**
     * @return the variable2
     */
    public String getVariable2() {
        return variable2;
    }

    /**
     * @param variable2 要设置的variable2
     */
    public void setVariable2(String variable2) {
        this.variable2 = variable2;
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
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(connector)) {
            sb.append(connector);
        }
        if (StringUtils.isNotBlank(leftBracket)) {
            sb.append(leftBracket);
        }
        if (StringUtils.isNotBlank(variable1)) {
            sb.append(variable1);
        }
        if (StringUtils.isNotBlank(operator)) {
            sb.append(operator);
        }
        if (StringUtils.isNotBlank(variable2)) {
            sb.append(variable2);
        }
        if (StringUtils.isNotBlank(rightBracket)) {
            sb.append(rightBracket);
        }
        return sb.toString();
    }

    /**
     * @return
     */
    public String getCondition() {
        StringBuilder sb = new StringBuilder();
        if (StringUtils.isNotBlank(connector)) {
            sb.append(Separator.SPACE.getValue());
            sb.append(connector);
        }
        // 表单域
        if (FORM_FIELD.equals(type)) {
            if (StringUtils.isNotBlank(leftBracket)) {
                sb.append(Separator.SPACE.getValue());
                sb.append(leftBracket);
            }
            if (StringUtils.isNotBlank(variable1)) {
                sb.append(Separator.SPACE.getValue());
                sb.append("dyform." + variable1);
            }
            if (StringUtils.isNotBlank(operator)) {
                sb.append(Separator.SPACE.getValue());
                sb.append(operator);
            }
            if (StringUtils.isNotBlank(variable2)) {
                sb.append(Separator.SPACE.getValue());
                sb.append(variable2);
            }
            if (StringUtils.isNotBlank(rightBracket)) {
                sb.append(Separator.SPACE.getValue());
                sb.append(rightBracket);
            }
        } else if (ORG_ID.equals(type)) {
            // 人员归属
            if (StringUtils.isNotBlank(leftBracket)) {
                sb.append(Separator.SPACE.getValue());
                sb.append(leftBracket);
            }
            if (StringUtils.isNotBlank(variable1)) {
                sb.append(Separator.SPACE.getValue());
                sb.append(variable1);
            }
            if (StringUtils.isNotBlank(operator)) {
                sb.append(Separator.SPACE.getValue());
                sb.append(operator);
            }
            if (StringUtils.isNotBlank(variable2)) {
                sb.append(Separator.SPACE.getValue());
                sb.append(variable2);
            }
            if (StringUtils.isNotBlank(rightBracket)) {
                sb.append(Separator.SPACE.getValue());
                sb.append(rightBracket);
            }
        } else if (EXPRESSION.equals(type)) {
            // 表达式
            if (StringUtils.isNotBlank(leftBracket)) {
                sb.append(Separator.SPACE.getValue());
                sb.append(leftBracket);
            }
            if (StringUtils.isNotBlank(expression)) {
                sb.append(Separator.SPACE.getValue());
                sb.append(expression);
            }
            if (StringUtils.isNotBlank(rightBracket)) {
                sb.append(Separator.SPACE.getValue());
                sb.append(rightBracket);
            }
        }
        return sb.toString();
    }

    public boolean isFormFieldCondition() {
        return FORM_FIELD.equals(type);
    }

    public boolean isMemberOfCondition() {
        return ORG_ID.equals(type);
    }

    public boolean isContainsCondition() {
        return CONTAINS.equals(operator);
    }

    public boolean isNotContainsCondition() {
        return NOT_CONTAINS.equals(operator);
    }

    public boolean isExpressionCondition() {
        return EXPRESSION.equals(type);
    }

}
