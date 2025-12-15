/*
 * @(#)2015年8月28日 V1.0
 *
 * Copyright 2015 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.bpm.engine.support;

import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.bpm.engine.enums.Participant;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;

/**
 * Description: 如何描述该类
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2015年8月28日.1	zhulh		2015年8月28日		Create
 * </pre>
 * @date 2015年8月28日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TaskUserExpressionConfig implements Serializable {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -2761326848563462986L;

    private static final String LEFT_BRACKET = "(";

    private static final String RIGHT_BRACKET = ")";

    // 左括号
    private String leftBracket;
    // 人员类型名称
    private String userTypeName;
    // 人员类型
    private String userType;
    // 人员名称
    private String userName;
    // 人员值
    private String userValue;
    // 人员值路径
    private String valuePath;
    // 组织版本类型
    private String orgVersionType;
    // 组织版本ID
    private String orgVersionId;
    // 组织版本名称
    private String orgVersionName;
    //	// 组织机构名称
    //	private String orgName;
    //	// 组织机构ID
    //	private String orgId;
    // 选项筛选名称
    private String optionOfName;
    // 选项筛选值
    private String optionOf;
    // 集合操作名称
    private String setOperationName;
    // 集合操作值
    private String setOperation;
    // 右括号
    private String rightBracket;
    // 排序号
    private Integer order;

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
     * @return the userTypeName
     */
    public String getUserTypeName() {
        return userTypeName;
    }

    /**
     * @param userTypeName 要设置的userTypeName
     */
    public void setUserTypeName(String userTypeName) {
        this.userTypeName = userTypeName;
    }

    /**
     * @return the userType
     */
    public String getUserType() {
        return userType;
    }

    /**
     * @param userType 要设置的userType
     */
    public void setUserType(String userType) {
        this.userType = userType;
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName 要设置的userName
     */
    public void setUserName(String userName) {
        this.userName = userName;
    }

    /**
     * @return the userValue
     */
    public String getUserValue() {
        return userValue;
    }

    /**
     * @param userValue 要设置的userValue
     */
    public void setUserValue(String userValue) {
        this.userValue = userValue;
    }

    /**
     * @return the valuePath
     */
    public String getValuePath() {
        return valuePath;
    }

    /**
     * @param valuePath 要设置的valuePath
     */
    public void setValuePath(String valuePath) {
        this.valuePath = valuePath;
    }

    /**
     * @return the orgVersionType
     */
    public String getOrgVersionType() {
        return orgVersionType;
    }

    /**
     * @param orgVersionType 要设置的orgVersionType
     */
    public void setOrgVersionType(String orgVersionType) {
        this.orgVersionType = orgVersionType;
    }

    /**
     * @return the orgVersionId
     */
    public String getOrgVersionId() {
        return orgVersionId;
    }

    /**
     * @param orgVersionId 要设置的orgVersionId
     */
    public void setOrgVersionId(String orgVersionId) {
        this.orgVersionId = orgVersionId;
    }

    /**
     * @return the orgVersionName
     */
    public String getOrgVersionName() {
        return orgVersionName;
    }

    /**
     * @param orgVersionName 要设置的orgVersionName
     */
    public void setOrgVersionName(String orgVersionName) {
        this.orgVersionName = orgVersionName;
    }

    //	/**
    //	 * @return the orgName
    //	 */
    //	public String getOrgName() {
    //		return orgName;
    //	}
    //
    //	/**
    //	 * @param orgName 要设置的orgName
    //	 */
    //	public void setOrgName(String orgName) {
    //		this.orgName = orgName;
    //	}
    //
    //	/**
    //	 * @return the orgId
    //	 */
    //	public String getOrgId() {
    //		return orgId;
    //	}
    //
    //	/**
    //	 * @param orgId 要设置的orgId
    //	 */
    //	public void setOrgId(String orgId) {
    //		this.orgId = orgId;
    //	}

    /**
     * @return the optionOfName
     */
    public String getOptionOfName() {
        return optionOfName;
    }

    /**
     * @param optionOfName 要设置的optionOfName
     */
    public void setOptionOfName(String optionOfName) {
        this.optionOfName = optionOfName;
    }

    /**
     * @return the optionOf
     */
    public String getOptionOf() {
        return optionOf;
    }

    /**
     * @param optionOf 要设置的optionOf
     */
    public void setOptionOf(String optionOf) {
        this.optionOf = optionOf;
    }

    /**
     * @return the setOperationName
     */
    public String getSetOperationName() {
        return setOperationName;
    }

    /**
     * @param setOperationName 要设置的setOperationName
     */
    public void setSetOperationName(String setOperationName) {
        this.setOperationName = setOperationName;
    }

    /**
     * @return the setOperation
     */
    public String getSetOperation() {
        return setOperation;
    }

    /**
     * @param setOperation 要设置的setOperation
     */
    public void setSetOperation(String setOperation) {
        this.setOperation = setOperation;
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
     * @return the order
     */
    public Integer getOrder() {
        return order;
    }

    /**
     * @param order 要设置的order
     */
    public void setOrder(Integer order) {
        this.order = order;
    }

    /**
     * @return
     */
    public String getExpression(boolean useValuePath) {
        StringBuilder sb = new StringBuilder();
        // 集合操作
        if (StringUtils.isNotBlank(setOperation)) {
            sb.append(Separator.SPACE.getValue());
            sb.append(setOperation);
            sb.append(Separator.SPACE.getValue());
        }
        // 左括号
        if (StringUtils.isNotBlank(leftBracket)) {
            sb.append(leftBracket);
        }
        // 组织类型
        boolean isOrgId = false;
        if (StringUtils.isNotBlank(orgVersionId) && StringUtils.isBlank(optionOf)) {
            sb.append(Participant.OrganizationOf.name() + LEFT_BRACKET);
            isOrgId = true;
        }
        // 选项筛选
        boolean isOptionOf = false;
        if (StringUtils.isNotBlank(optionOf)) {
            sb.append(optionOf);
            isOptionOf = true;
        }
        if (isOptionOf) {
            sb.append(LEFT_BRACKET);
        }
        // 人员类型
        boolean isUuserType = false;
        if (StringUtils.isNotBlank(userType)) {
            sb.append(userType);
            isUuserType = true;
        }
        if (isUuserType) {
            sb.append(LEFT_BRACKET);
        }
        // 人员值
        if (StringUtils.isNotBlank(userValue)) {
            sb.append(Separator.SINGLE_QUOTES.getValue());
            if (useValuePath && StringUtils.isNotBlank(valuePath)) {
                sb.append(StringUtils.replace(valuePath, ";", ","));
            } else {
                sb.append(StringUtils.replace(userValue, ";", ","));
            }
            sb.append(Separator.SINGLE_QUOTES.getValue());
        }
        if (isUuserType) {
            sb.append(RIGHT_BRACKET);
        }
        if (isOptionOf) {
            if (StringUtils.isNotBlank(orgVersionId)) {
                sb.append(",'" + orgVersionType + ":" + orgVersionId + "'");
            } else {
                sb.append(",''");
            }
            sb.append(RIGHT_BRACKET);
        }
        if (isOrgId) {
            sb.append(",'" + orgVersionType + ":" + orgVersionId + "'");
            sb.append(RIGHT_BRACKET);
        }
        // 右括号
        if (StringUtils.isNotBlank(rightBracket)) {
            sb.append(rightBracket);
        }
        return sb.toString();
    }

    /**
     * @return
     */
    public String getExpressionDisplayName() {
        StringBuilder sb = new StringBuilder();
        // 集合操作
        if (StringUtils.isNotBlank(setOperation)) {
            sb.append(Separator.SPACE.getValue());
            sb.append(setOperationName);
            sb.append(Separator.SPACE.getValue());
        }
        // 左括号
        if (StringUtils.isNotBlank(leftBracket)) {
            sb.append(leftBracket);
        }
        // 组织类型
        if (StringUtils.isNotBlank(orgVersionName)) {
            sb.append(orgVersionName + "的");
        }
        if (StringUtils.isNotBlank(userName)) {
            sb.append(userTypeName + "(" + userName + ")");
        }
        if (StringUtils.isNotBlank(optionOfName)) {
            sb.append("的" + optionOfName);
        }
        // 右括号
        if (StringUtils.isNotBlank(rightBracket)) {
            sb.append(rightBracket);
        }
        return sb.toString();
    }

}
