/*
 * @(#)2013-1-18 V1.0
 *
 * Copyright 2013 WELL-SOFT, Inc. All rights reserved.
 */
package com.wellsoft.context.enums;

import org.apache.commons.lang.StringUtils;

/**
 * Description: 标识实体ID的前缀
 *
 * @author zhulh
 * @version 1.0
 *
 * <pre>
 * 修改记录:
 * 修改后版本	修改人		修改日期			修改内容
 * 2013-1-18.1	zhulh		2013-1-18		Create
 * </pre>
 * @date 2013-1-18
 */
public enum IdPrefix {
    USER("User", "U"), // 用户
    DEPARTMENT("Department", "D"), // 部门
    GROUP("Group", "G"), // 群组
    SYSTEM_UNIT("System Unit", "S"), // 系统单位
    ROLE("Role", "R"), // 角色
    PRIVILEGE("Privilege", "P"), // 权限
    TENANT("Multi Tenant", "T"), // 租户
    JOB("Job", "J"), // 职位
    DUTY("Duty", "DU"), // 职务
    RANK("Rank", "R"), // 职级
    ORG("Org", "O"), // 系统组织
    ORG_VERSION("ORG", "V"), // 组织版本
    BUSINESS_UNIT("Business Unit", "B"), // 业务单位
    EXTERNAL("External", "E"), // 外部单位
    CATEGORY("Category", "C"), // 分类
    EXTEND_ELEMENT("Extend Org Element", "EE"),// 自定义扩展的组织单元模型类型

    DOCEXC_CONTACT_UNIT("DocExcContactUnit", "UI_DEC"), // 通讯录单位ID
    DOCEXC_CONTACT("DocExcContact", "CI_DEC"), // 通讯录联系人ID

    CONTACT_BOOK("ContactBook", "CBI"), // 邮件联系人ID前缀
    CONTACT_BOOK_GRP("ContactBookGrp", "CBGI"),//邮件联系人分组ID前缀

    MULTI_GROUP("MultiGroup", "MG"),//集团
    MULTI_GROUP_CATEGORY("MultiGroupCategory", "MC"),//集团节点分类

    PARTER_SYS_ORG("ParterSysOrg", "PSO"), // 协作系统组织（外部组织）

    ORG_USER("OrgUser", "OU"),// 组织用户

    BIZ_ORG("BizOrg", "BO"), // 业务组织

    BIZ_ORG_DIM("BizOrgDim", "BOD"), // 业务组织分配维度

    BIZ_ORG_ROLE("BizOrgRole", "BOR"),// 业务组织角色

    BIZ_PREFIX("Biz", "BIZ");


    private String name;
    private String value;

    /**
     * 如何描述该构造方法
     *
     * @param name
     * @param value
     */
    private IdPrefix(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public static boolean startsWithExternal(String id) {
        if (id.startsWith(EXTERNAL.getValue())) {
            return true;
        }
        if (id.startsWith(CATEGORY.getValue())) {
            return true;
        }
        return false;
    }

    public static boolean startsDocExc(String id) {
        if (id.startsWith(DOCEXC_CONTACT_UNIT.getValue())) {
            return true;
        }
        if (id.startsWith(DOCEXC_CONTACT.getValue())) {
            return true;
        }
        return false;
    }

    public static boolean startsContactBook(String id) {
        if (id.startsWith(CONTACT_BOOK.getValue())) {
            return true;
        }
        if (id.startsWith(CONTACT_BOOK_GRP.getValue())) {
            return true;
        }
        return false;
    }

    public static boolean startsUser(String id) {
        return StringUtils.startsWith(id, USER.getValue());
    }

    public static boolean hasPrefix(String id) {
        IdPrefix[] values = values();
        for (IdPrefix prefix : values) {
            if (StringUtils.startsWith(id, prefix.getValue())) {
                return true;
            }
        }
        return false;
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

}
