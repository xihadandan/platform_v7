/*
 * @(#)2019年10月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import com.wellsoft.context.enums.Separator;
import com.wellsoft.pt.security.core.userdetails.UserDetails;
import com.wellsoft.pt.security.util.SpringSecurityUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;

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
 * 2019年10月12日.1	zhulh		2019年10月12日		Create
 * </pre>
 * @date 2019年10月12日
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DataRuleDefinition extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = 422715718129354977L;

    private static final String LEFT_BRACKET = "(";

    private static final String RIGHT_BRACKET = ")";

    private static final String GROUP_TYPE = "1";

    private static final String ROLE_RULE = "1";

    // ID
    private String id;

    // 名称
    private String name;

    // 类型1分组、2规则
    private String type;

    // 规则类别1角色归属、2字段值比较
    private String ruleType;

    // 字段名
    private String fieldName;

    // 操作符
    private String operator;

    // 字段值
    private String fieldValue;

    // 角色ID
    private String roleId;

    // 连接符
    private String connector;

    // 嵌套的数据规则
    private List<DataRuleDefinition> rules = Lists.newArrayList();

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id 要设置的id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name 要设置的name
     */
    public void setName(String name) {
        this.name = name;
    }

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
     * @return the ruleType
     */
    public String getRuleType() {
        return ruleType;
    }

    /**
     * @param ruleType 要设置的ruleType
     */
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
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
     * @return the fieldValue
     */
    public String getFieldValue() {
        return fieldValue;
    }

    /**
     * @param fieldValue 要设置的fieldValue
     */
    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    /**
     * @return the roleId
     */
    public String getRoleId() {
        return roleId;
    }

    /**
     * @param roleId 要设置的roleId
     */
    public void setRoleId(String roleId) {
        this.roleId = roleId;
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
     * @return the rules
     */
    public List<DataRuleDefinition> getRules() {
        return rules;
    }

    /**
     * @param rules 要设置的rules
     */
    public void setRules(List<DataRuleDefinition> rules) {
        this.rules = rules;
    }

    /**
     * 规则转SQL语句
     *
     * @return
     */
    public String toSqlString() {
        StringBuilder sb = new StringBuilder();
        // 连接符
        if (StringUtils.isNotBlank(connector)) {
            sb.append(Separator.SPACE.getValue()).append(connector).append(Separator.SPACE.getValue());
        }

        // 规则分组
        boolean ruleGroup = GROUP_TYPE.equals(type);
        // 1、分组处理
        if (ruleGroup) {
            // 分组左括号
            sb.append(LEFT_BRACKET);
            // 子规则
            if (CollectionUtils.isNotEmpty(rules)) {
                for (DataRuleDefinition dataRule : rules) {
                    sb.append(dataRule.toSqlString());
                }
            } else {
                sb.append("1 = 1");
            }
            // 分组右括号
            sb.append(RIGHT_BRACKET);
        } else {
            // 2、规则处理
            // 2.1、角色归属
            if (ROLE_RULE.equals(ruleType)) {
                UserDetails userDetails = SpringSecurityUtils.getCurrentUser();
                if (hasRole(userDetails, roleId)) {
                    sb.append("1 = 1");
                } else {
                    sb.append("1 = 2");
                }
            } else {
                // 2.2、字段值比较
                sb.append(fieldName).append(operator).append(fieldValue);
            }
        }
        return sb.toString();
    }

    /**
     * @param userDetails
     * @param configRoleId
     * @return
     */
    private boolean hasRole(UserDetails userDetails, String configRoleId) {
        if (StringUtils.isBlank(configRoleId)) {
            return true;
        }
        for (GrantedAuthority authority : userDetails.getAuthorities()) {
            if (StringUtils.equals(authority.getAuthority(), configRoleId)) {
                return true;
            }
        }
        return false;
    }

}
