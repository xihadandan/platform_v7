/*
 * @(#)2019年10月12日 V1.0
 *
 * Copyright 2019 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.pt.dms.dp.support;

import com.google.common.collect.Lists;
import com.wellsoft.context.base.BaseObject;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

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
public class DataRangeDefinition extends BaseObject {

    /**
     * 如何描述serialVersionUID
     */
    private static final long serialVersionUID = -8148137513920598205L;

    // 当前用户组织信息对象类别
    public static String CURRENT_USER_INFO_TYPE = "1";
    // 指定组织结点
    public static String SPECIFY_ORG_NODE_TYPE = "2";
    // 按数据规则过滤
    public static String DATA_RULE_TYPE = "3";

    // ID
    private String id;

    // 名称
    private String name;

    // 对象类别:1当前用户组织信息，2指定组织结点，3按数据规则过滤
    private String type;

    // 数据所有者
    private String owner;

    // 包含上级组织节点
    private boolean includeSuperiorOrg;

    // 包含同级组织节点
    private boolean includeSiblingOrg;

    // 包含下级组织节点
    private boolean includeSubordinateOrg;

    // 数据所有者字段
    private String ownerFieldName;

    // 归属角色
    private String roleId;

    // 数据规则
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
     * @return the owner
     */
    public String getOwner() {
        return owner;
    }

    /**
     * @param owner 要设置的owner
     */
    public void setOwner(String owner) {
        this.owner = owner;
    }

    /**
     * @return the includeSuperiorOrg
     */
    public boolean isIncludeSuperiorOrg() {
        return includeSuperiorOrg;
    }

    /**
     * @param includeSuperiorOrg 要设置的includeSuperiorOrg
     */
    public void setIncludeSuperiorOrg(boolean includeSuperiorOrg) {
        this.includeSuperiorOrg = includeSuperiorOrg;
    }

    /**
     * @return the includeSiblingOrg
     */
    public boolean isIncludeSiblingOrg() {
        return includeSiblingOrg;
    }

    /**
     * @param includeSiblingOrg 要设置的includeSiblingOrg
     */
    public void setIncludeSiblingOrg(boolean includeSiblingOrg) {
        this.includeSiblingOrg = includeSiblingOrg;
    }

    /**
     * @return the includeSubordinateOrg
     */
    public boolean isIncludeSubordinateOrg() {
        return includeSubordinateOrg;
    }

    /**
     * @param includeSubordinateOrg 要设置的includeSubordinateOrg
     */
    public void setIncludeSubordinateOrg(boolean includeSubordinateOrg) {
        this.includeSubordinateOrg = includeSubordinateOrg;
    }

    /**
     * @return the ownerFieldName
     */
    public String getOwnerFieldName() {
        return ownerFieldName;
    }

    /**
     * @param ownerFieldName 要设置的ownerFieldName
     */
    public void setOwnerFieldName(String ownerFieldName) {
        this.ownerFieldName = ownerFieldName;
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

}
